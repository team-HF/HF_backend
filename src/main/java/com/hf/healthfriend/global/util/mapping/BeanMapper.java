package com.hf.healthfriend.global.util.mapping;

import com.hf.healthfriend.global.util.mapping.exception.MappingException;
import com.hf.healthfriend.global.util.mapping.exception.MappingNotExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BeanMapper {
    private static final String BASE_PACKAGE = "com.hf.healthfriend";
    private static final Map<Class<?>, Map<Class<?>, Map<Field, Field>>> mappingTable = new HashMap<>();
    private static final Map<Class<?>, List<Class<?>>> classMapping = new HashMap<>();
    private static final Map<Field, Boolean> setNullMap = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(BeanMapping.class));

        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(BASE_PACKAGE);
        for (BeanDefinition beanDefinition : candidateComponents) {
            Class<?> mappingClass = Class.forName(beanDefinition.getBeanClassName());
            BeanMapping beanMapping = mappingClass.getAnnotation(BeanMapping.class);
            Class<?>[] targetClasses = beanMapping.value();
            for (Class<?> targetClass : targetClasses) {
                if (mappingClass == targetClass) {
                    throw new MappingException(String.format("""
                        Mapping Class와 Target Class가 같습니다.
                        Mapping Class: %s
                        Target Class: %s
                        """, mappingClass, targetClass));
                }

                Map<String, Field> targetFieldsByName = Arrays.stream(targetClass.getDeclaredFields())
                        .collect(Collectors.toMap(
                                Field::getName,
                                (field) -> field,
                                (f1, f2) -> f2
                        ));

                boolean isIncludeReverseMapping = beanMapping.includeReverseMapping();
                Map<Field, Field> targetFieldsByMappingField = new HashMap<>();
                Map<Field, Field> mappingFieldsByTargetField = new HashMap<>();
                for (Field mappingField : mappingClass.getDeclaredFields()) {
                    String targetFieldName;
                    boolean setNull;
                    boolean reverseSetNull;
                    if (mappingField.getAnnotation(MappingAttribute.class) != null) {
                        MappingAttribute mappingAttributeAnnotation = mappingField.getAnnotation(MappingAttribute.class);
                        targetFieldName = mappingAttributeAnnotation.target();
                        setNull = mappingAttributeAnnotation.setNull();
                        reverseSetNull = mappingAttributeAnnotation.reverseSetNull();
                    } else {
                        targetFieldName = mappingField.getName();
                        setNull = true;
                        reverseSetNull = true;
                    }

                    if (targetFieldsByName.containsKey(targetFieldName)) {
                        Field targetField = targetFieldsByName.get(targetFieldName);
                        targetFieldsByMappingField.put(mappingField, targetField);
                        setNullMap.put(mappingField, setNull);
                        if (isIncludeReverseMapping) {
                            mappingFieldsByTargetField.put(targetField, mappingField);
                            setNullMap.put(targetField, reverseSetNull);
                        }
                    } else {
                        log.debug("Mapping attribute에 해당하는 Target attribute가 없음; mappingClass={}, targetClass={}, fieldName={}",
                                mappingClass, targetClass, targetFieldName);
                    }
                }

                log.debug("mappingClass={}, isIncludeReverseMapping={}", mappingClass, isIncludeReverseMapping);

                put(targetClass, mappingClass, targetFieldsByMappingField);
                if (isIncludeReverseMapping) {
                    put(mappingClass, targetClass, mappingFieldsByTargetField);
                }
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("Mapping 정보");
            for (Map.Entry<Class<?>, Map<Class<?>, Map<Field, Field>>> mappedTo : mappingTable.entrySet()) {
                for (Map.Entry<Class<?>, Map<Field, Field>> entry : mappedTo.getValue().entrySet()) {
                    System.out.println("Mapping class=" + mappedTo.getKey());
                    System.out.println("Target class=" + entry.getKey());
                    for (Map.Entry<Field, Field> fieldEntry : entry.getValue().entrySet()) {
                        System.out.println(fieldEntry.getKey().getType().getSimpleName() + " " + fieldEntry.getKey().getName()
                                + " ====>> " + fieldEntry.getValue().getType().getSimpleName() + " " + fieldEntry.getValue().getName());
                    }
                }
                System.out.println("--------------------------");
            }
        }
    }

    private void put(Class<?> mappingClass, Class<?> targetClass, Map<Field, Field> mappingFieldsByTargetField) {
        if (mappingTable.containsKey(targetClass)) {
            mappingTable.get(targetClass).put(mappingClass, mappingFieldsByTargetField);
        } else {
            Map<Class<?>, Map<Field, Field>> map = new HashMap<>();
            map.put(mappingClass, mappingFieldsByTargetField);
            mappingTable.put(targetClass, map);
        }
        if (classMapping.containsKey(targetClass)) {
            classMapping.get(targetClass).add(mappingClass);
        } else {
            List<Class<?>> targets = new ArrayList<>();
            targets.add(mappingClass);
            classMapping.put(targetClass, targets);
        }
    }

    public <S> Object generateBean(S from) {
        Class<?> mappingClass = from.getClass();
        Class<?> targetClass = classMapping.get(mappingClass).get(0);
        return generateBean(from, targetClass);
    }

    public <S, T> T generateBean(S from, Class<? extends T> targetClass) {
        Map<Field, Field> fieldMappings = getFieldMappings(from.getClass(), targetClass);

        try {
            Constructor<? extends T> targetConstructor = targetClass.getDeclaredConstructor();
            targetConstructor.setAccessible(true);
            T targetInstance = targetConstructor.newInstance();
            targetConstructor.setAccessible(false);
            for (Map.Entry<Field, Field> entry : fieldMappings.entrySet()) {
                Field mappingField = entry.getKey();
                Field targetField = entry.getValue();
                mappingField.setAccessible(true);
                targetField.setAccessible(true);

                System.out.println("asdf=" + mappingField.get(from));
                System.out.println("targetField=" + targetField);

                targetField.set(targetInstance, mappingField.get(from));

                mappingField.setAccessible(false);
                mappingField.setAccessible(false);
            }
            return targetInstance;
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    public <S, T> void copyProperties(S source, T target) {
        Map<Field, Field> fieldMappings = getFieldMappings(source.getClass(), target.getClass());

        try {
            for (Map.Entry<Field, Field> entry : fieldMappings.entrySet()) {
                Field mappingField = entry.getKey();
                Field targetField = entry.getValue();
                mappingField.setAccessible(true);
                targetField.setAccessible(true);

                Object mappingFieldValue = mappingField.get(source);
                if (mappingFieldValue != null || setNullMap.get(mappingField)) {
                    targetField.set(target, mappingFieldValue);
                }

                mappingField.setAccessible(false);
                mappingField.setAccessible(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <S, T> Map<Field, Field> getFieldMappings(Class<? extends S> sourceClass, Class<? extends T> targetClass) {
        Map<Field, Field> fieldMappings = mappingTable.get(sourceClass).get(targetClass);
        if (fieldMappings == null || targetClass == null || !classMapping.get(sourceClass).contains(targetClass)) {
            throw new MappingNotExistsException(
                    "매핑이 존재하지 않습니다; source class=" + sourceClass + ", target class=" + targetClass
            );
        }
        return fieldMappings;
    }
}

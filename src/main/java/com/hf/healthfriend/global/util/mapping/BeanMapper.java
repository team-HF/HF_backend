package com.hf.healthfriend.global.util.mapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BeanMapper {
    private static final String BASE_PACKAGE = "com.hf.healthfriend";
    private static final Map<Class<?>, Map<Field, Field>> mappingTable = new HashMap<>();
    private static final Map<Class<?>, Class<?>> classMapping = new HashMap<>();
    private static final Map<Field, Boolean> setNullMap = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(BeanMapping.class));

        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(BASE_PACKAGE);
        for (BeanDefinition beanDefinition : candidateComponents) {
            Class<?> mappingClass = Class.forName(beanDefinition.getBeanClassName());
            Class<?> targetClass = mappingClass.getAnnotation(BeanMapping.class).value();

            classMapping.put(mappingClass, targetClass);

            Map<String, Field> targetFieldsByName = Arrays.stream(targetClass.getDeclaredFields())
                    .collect(Collectors.toMap(
                            Field::getName,
                            (field) -> field,
                            (f1, f2) -> f2
                    ));

            Map<Field, Field> targetFieldsByMappingField = new HashMap<>();
            for (Field mappingField : mappingClass.getDeclaredFields()) {
                String targetFieldName;
                boolean setNull;
                if (mappingField.getAnnotation(MappingAttribute.class) != null) {
                    MappingAttribute mappingAttributeAnnotation = mappingField.getAnnotation(MappingAttribute.class);
                    targetFieldName = mappingAttributeAnnotation.target();
                    setNull = mappingAttributeAnnotation.setNull();
                } else {
                    targetFieldName = mappingField.getName();
                    setNull = true;
                }

                if (targetFieldsByName.containsKey(targetFieldName)) {
                    targetFieldsByMappingField.put(mappingField, targetFieldsByName.get(targetFieldName));
                    setNullMap.put(mappingField, setNull);
                } else {
                    log.debug("Mapping attribute에 해당하는 Target attribute가 없음");
                }
            }
            mappingTable.put(mappingClass, targetFieldsByMappingField);
        }

        if (log.isTraceEnabled()) {
            log.trace("Mapping 정보");
            for (Map.Entry<Class<?>, Map<Field, Field>> entry : mappingTable.entrySet()) {
                System.out.println("Mapping class=" + entry.getKey());
                System.out.println("Target class=" + classMapping.get(entry.getKey()));
                for (Map.Entry<Field, Field> fieldEntry : entry.getValue().entrySet()) {
                    System.out.println(fieldEntry.getKey().getType().getSimpleName() + " " + fieldEntry.getKey().getName()
                            + " ====>> " + fieldEntry.getValue().getType().getSimpleName() + " " + fieldEntry.getValue().getName());
                }
                System.out.println("--------------------------");
            }
        }
    }

    public <M> Object generateBean(M from) {
        Class<?> mappingClass = from.getClass();
        Class<?> targetClass = classMapping.get(mappingClass);
        return generateBean(from, targetClass);
    }

    public <M, T> T generateBean(M from, Class<? extends T> targetClass) {
        Map<Field, Field> fieldMappings = mappingTable.get(from.getClass());

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

                targetField.set(targetInstance, mappingField.get(from));

                mappingField.setAccessible(false);
                mappingField.setAccessible(false);
            }
            return targetInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <S, T> void copyProperties(S source, T target) {
        Map<Field, Field> fieldMappings = mappingTable.get(source.getClass());

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
}
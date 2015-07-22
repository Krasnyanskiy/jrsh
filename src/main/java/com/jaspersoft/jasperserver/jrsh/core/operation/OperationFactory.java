package com.jaspersoft.jasperserver.jrsh.core.operation;

import com.jaspersoft.jasperserver.jrsh.core.common.MetadataScannerConfig;
import com.jaspersoft.jasperserver.jrsh.core.operation.annotation.Master;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception.CouldNotCreateOperationInstanceException;
import com.jaspersoft.jasperserver.jrsh.core.operation.parser.exception.OperationNotFoundException;
import lombok.extern.log4j.Log4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.FilterBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j
public class OperationFactory {

    private static final Map<String, Class<? extends Operation>> operations;
    public static final String basePackage = "com.jaspersoft.jasperserver.jrsh.core.operation.impl";

    static {
        operations = new HashMap<String, Class<? extends Operation>>();
        for (val operationType : getOperationTypes()) {
            Master annotation = operationType.getAnnotation(Master.class);
            if (annotation != null) {
                String operationName = annotation.name();
                operations.put(operationName, operationType);
            }
        }
    }

    public static Operation createOperationByName(String operationName) {
        val operationType = operations.get(operationName);
        if (operationType == null) {
            throw new OperationNotFoundException();
        }
        return createInstance(operationType);
    }

    public static Set<Operation> createOperationsByAvailableTypes() {
        val setOfOperations = new HashSet<Operation>();
        for (val type : operations.values()) {
            setOfOperations.add(createInstance(type));
        }
        return setOfOperations;
    }

    protected static Operation createInstance(Class<? extends Operation> operationType) {
        try {
            return operationType.newInstance();
        } catch (Exception err) {
            throw new CouldNotCreateOperationInstanceException(err);
        }
    }

    protected static Set<Class<? extends Operation>> getOperationTypes() {
        val operationTypes = new HashSet<Class<? extends Operation>>();
        Yaml yml = new Yaml();

        InputStream scanner = OperationFactory.class.getClassLoader().getResourceAsStream("scanner.yml");
        MetadataScannerConfig config = yml.loadAs(scanner, MetadataScannerConfig.class);
        List<String> packagesToScan = config.getPackagesToScan();
        List<String> classes = config.getClasses();
        FilterBuilder filter = new FilterBuilder().includePackage(basePackage);

        if (packagesToScan != null) {
            for (String aPackage : packagesToScan) {
                aPackage = StringUtils.chomp(aPackage, ".*");
                filter.includePackage(aPackage);
            }
        }

        if (classes != null) {
            for (String aClass : classes) {
                try {
                    Class clz = Class.forName(aClass);
                    if (!Modifier.isAbstract(clz.getModifiers())
                            && Operation.class.isAssignableFrom(clz)) {
                        operationTypes.add(clz);
                    }
                } catch (ClassNotFoundException ignored) {
                }
            }
        }

        Reflections ref = new Reflections(new SubTypesScanner(), filter);
        for (val subType : ref.getSubTypesOf(Operation.class)) {
            if (!Modifier.isAbstract(subType.getModifiers())) {
                operationTypes.add(subType);
            }
        }
        return operationTypes;
    }
}

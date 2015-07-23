/*
 * Copyright (C) 2005 - 2015 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.jrsh.operation;

import com.jaspersoft.jasperserver.jrsh.core.common.MetadataScannerConfig;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.FilterBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OperationTypeReader {

    public static Set<Class<? extends Operation>> readTypes(String basePackage) {
        Set<Class<? extends Operation>> operationTypes = new HashSet<Class<? extends Operation>>();
        Yaml yml = new Yaml();

        InputStream scanner = OperationTypeReader.class.getClassLoader().getResourceAsStream("scanner.yml");
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
        for (Class<? extends Operation> subType : ref.getSubTypesOf(Operation.class)) {
            if (!Modifier.isAbstract(subType.getModifiers())) {
                operationTypes.add(subType);
            }
        }
        return operationTypes;
    }

}

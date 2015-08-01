//package com.jaspersoft.jasperserver.jrsh.operation.impl;
//
//import org.apache.commons.lang3.SystemUtils;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import java.io.File;
//
//import static java.lang.System.getProperty;
//import static org.mockito.Mockito.reset;
//import static org.mockito.Mockito.times;
//import static org.mockito.MockitoAnnotations.initMocks;
//import static org.powermock.api.mockito.PowerMockito.*;
//
///**
// * Unit tests for {@link ImportOperation} class. For Unix-like OS only.
// */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({File.class, ImportOperation.class})
//public class ImportOperationTest {
//
//    private ImportOperation importOperation;
//
//    @Mock private File fileMock;
//
//    @Before
//    public void before() {
//        importOperation = new ImportOperation();
//        initMocks(fileMock);
//    }
//
//    @Test
//    public void shouldReplaceTildeCharacterWithUserHomeDirectoryIfPassAFileThatBeginsWithATilde() throws Exception {
//        if (!SystemUtils.IS_OS_WINDOWS) {
//
//            // given
//            String file = "~/Downloads/jrsh/file.zip";
//            importOperation.setPath(file);
//            whenNew(File.class).withArguments(file).thenReturn(fileMock);
//            doReturn(false).when(fileMock).isDirectory();
//            doReturn(false).when(fileMock).isFile();
//
//            // when
//            importOperation.execute(null);
//
//            // then
//            String fullPath = file.replaceFirst("^~", getProperty("user.home"));
//            verifyNew(File.class, times(1)).withArguments(fullPath);
//        }
//    }
//
//    @Test
//    public void shouldExecuteOperationWithoutReplacingTildeCharacterIfFileDoesNotBeginWithTilde() throws Exception {
//        if (!SystemUtils.IS_OS_WINDOWS) {
//
//            // given
//            String file = "/Users/alex/Downloads/jrsh/file.zip";
//            whenNew(File.class).withArguments(file).thenReturn(fileMock);
//            importOperation.setPath(file);
//
//            doReturn(false).when(fileMock).isDirectory();
//            doReturn(false).when(fileMock).isFile();
//
//            // when
//            importOperation.execute(null);
//
//            // then
//            verifyNew(File.class, times(1)).withArguments(file);
//        }
//    }
//
//    @After
//    public void after() {
//        reset(fileMock);
//    }
//}

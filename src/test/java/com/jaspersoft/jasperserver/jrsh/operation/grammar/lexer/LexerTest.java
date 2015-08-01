//package com.jaspersoft.jasperserver.jrsh.operation.grammar.lexer;
//
//import org.junit.Assert;
//import org.junit.Test;
//
///**
// * Unit tests for {@link PathConsideringLexer} class.
// */
//public class LexerTest {
//
//    public static final String OPERATION_1 =
//            "import ~/Downloads/folder/New\\ Folder/file.zip with-include-audit-events ";
//
//    public static final String OPERATION_2 =
//            "import ~/Downloads/folder/New\\ Folder/My\\ Another\\ Cool\\ Folder/file.zip";
//
//    private Lexer lexer = new PathConsideringLexer();
//
//    @Test
//    public void shouldRecognizePathWithOneSpaceAsSingleToken() {
//        // when
//        int tokensAmount = lexer.convert(OPERATION_1).size();
//
//        // then
//        Assert.assertSame(tokensAmount, 3);
//    }
//
//    @Test
//    public void shouldRecognizePathWithFourSpacesAsSingleToken() {
//        // when
//        int tokensAmount = lexer.convert(OPERATION_2).size();
//
//        // then
//        Assert.assertSame(tokensAmount, 2);
//    }
//}

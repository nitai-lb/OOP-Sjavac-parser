package oop.ex6.main;


import oop.ex6.Block.*;
import oop.ex6.BlockException;
import oop.ex6.IllegalLineException;
import oop.ex6.VariableException;
import oop.ex6.Variables.Variable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private static final int VARIABLE_DECLARATION_NUM = 3, VARIABLE_ASSIGNMENT_NUM = 4, METHOD_DECLARATION_NUM = 5,
            METHOD_CALL_NUM = 6, IF_WHILE_NUM = 7, METHOD_CLOSER = 8, RETURN_NUM = 9, CURLY_BRACKET_NUM = 10,
            STRING_DEC_NUM = 11, CHAR_DEC_NUM = 12, DOUBLE_DEC_NUM = 13, BOOLEAN_DEC_NUM = 14, INT_DEC_NUM = 15;
    //*********

    private static final int BLANK = 0, WRONG = 1, COMMENT = 2, CODE = 3;

    private static final int VARIABLE_GROUP = 3, NAME = 0, VALUE = 1, ARRAY_LENGTH = 2, GROUP_NAME = 1, GROUP_VALUE = 2,
    SECONDARY_GROUP_VALUE = 3;
    //*********


    ///////////////////
    // REGEX
    ///////////////////
    public static final String VARIABLE_NAME = "[ ]*(_[\\w]+|[A-Za-z]+[\\w]*)";///////////////////////
    static final String FINAL = "^(final )?";
    static final String INT_DECLARATION = "^[\\s]*(final[ ]+)?(int)[ ]+(((_[\\w]+|[A-Za-z]+[\\w]*)[ ]*((|=[ ]*((-)?[\\d]+|_[\\w]+|[A-Za-z]+[\\w]*))[ ]*,[ ]*))*(_[\\w]+|[A-Za-z]+[\\w]*)[ ]*(|=[ ]*((-)?[\\d]+|_[\\w]+|[A-Za-z]+[\\w]*)))[ ]*;[ ]*$";
    static final String STRING_DECLARATION = "^[ ]*(final[ ]+)?(String)[ ]+(((_\\w+|[A-Za-z]\\w*)[ ]*(|=[ ]*(\"[^\"]*\"|(_\\w+|[A-Za-z]\\w*)))[ ]*,[ ]*)*(_\\w+|[A-Za-z]\\w*)[ ]*(|=[ ]*(\"[^\"]*\"|(_\\w+|[A-Za-z]\\w*))))[ ]*;[ ]*$";
    static final String CHAR_DECLARATION = "^[\\s]*(final[ ]+)?(char)[ ]+(((_[\\w]+|[A-Za-z]+[\\w]*)(| = ('.?'|_[\\w]+|[A-Za-z]+[\\w]*)),[ ]*)*(_[\\w]+|[A-Za-z]+[\\w]*)[ ]*(|=[ ]*('.?'|_[\\w]+|[A-Za-z]+[\\w]*)[ ]*));[ ]*$";
    static final String DOUBLE_DECLARATION = "^[\\s]*(final[ ]+)?(double)[ ]+(((_[\\w]+|[A-Za-z][\\w]*)[ ]*(|=[ ]*((-)?([\\d]+\\.[\\d]+|[\\d]+)|_[\\w]+|[A-Za-z][\\w]*)),[ ]*)*(_[\\w]+|[A-Za-z][\\w]*)[ ]*(|=[ ]*((-)?([\\d]+\\.[\\d]+|[\\d]+)|_[\\w]|[A-Za-z][\\w]*)))[ ]*;[ ]*$";
    static final String BOOLEAN_DECLARATION = "^[\\s]*(final[ ]+)?(boolean)[ ]+(((_[\\w]+|[A-Za-z][\\w]*)[ ]*(|=[ ]*((-)?([\\d]+\\.[\\d]+|[\\d]+)|true|false|_[\\w]+|[A-Za-z][\\w]*))[ ]*,[ ]*)*[ ]*(_[\\w]+|[A-Za-z][\\w]*)[ ]*(|=[ ]*((-)?([\\d]+\\.[\\d]+|[\\d]+)|true|false|_[\\w]+|[A-Za-z][\\w]*)))[ ]*;[ ]*$";
    static final String METHOD_DECLARATION = "^[\\s]*(void )([A-Za-z][\\w]*)[ ]*\\(*([^\\)]*)\\)[ ]*\\{[ ]*$";
    static final String VARIABLE_ASSIGNMENT = "[\\s]*(_[\\w]+|[A-Za-z][\\w]*)[ ]*=[ ]*([^;|^ ]+)[ ]*;[ ]*$";
    static final String METHOD_CALL = "[\\s]*([A-Za-z]+[\\w]*)(\\(.*\\);)";
    static final String IF_WHILE = "^[\\s]*(if|while)[ ]*\\(([^)]*)\\)[ ]*\\{[ ]*$";
    static final String RETURN = "^[\\s]*return;[ ]*$";
    static final String CLOSING_CURLY_BRACKET = "[\\s]*}[ ]*";
//    static final String VARIABLE_ASSIGNMENT = "[ ]*(_[\\w]+|[A-Za-z]+[\\w]*)[ ]*=[ ]*(.*)[ ]*";
    static final String HAS_FINAL = "^\\s*(final) .*";
//    static final String VARIABLE_PARSER = "(_[\\w]+|[A-Za-z][\\w]*)[ ]*=[ ]*([^;|^ ]+)";
    static final String VARIABLE_PARSER = VARIABLE_NAME + "[ ]*(|=[ ]*([^ ]+)[ ]*)$";
    static final String ENDS_WITH = "[;{}][ ]*$";


    static ArrayList<String> lines = new ArrayList<>();
    static HashMap lineTypes = new HashMap();
    static FileReader buffer;
    static Pattern p;
    static Matcher m;
    static Global global;
    /**
     * A method which iterates over the text file for a first time, validating the Code and updating the global's
     * database. In the end each line is added to an arrayList which is used for the second iteration.
     *
     * @param file
     * @return
     * @throws IOException
     * @throws IllegalLineException
     */
    public static ArrayList firstLook(File file, Global firstGlobal) throws IOException, IllegalLineException, BlockException
            , VariableException {
        //Variable Declaration:
        buffer = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(buffer);
        String line;
        int lineType;
        int blockCounter = 0;
        String lastLine = null;
        global = firstGlobal;

        while ((line = reader.readLine()) != null) {
            lineType = getLineType(line);
////////////////////////////////////////////


            System.out.println(line); /////******* tests


//////////////////////////////////////////////////////////
            if (lineType == WRONG) {
                throw new IllegalLineException();
            } else if (lineType == CODE) {
                int codeType = getCodeType(line);
                if (codeType == WRONG) {
                    throw new IllegalLineException();
                }
                blockCounter = basicBlockChecker(codeType, blockCounter, lastLine, line);
                // checks all block opening scenarios.
                if (codeType == VARIABLE_DECLARATION_NUM && blockCounter == 0) {
                    createGlobalVariables(line);
                    // takes care of all variable declaration scenarios.
                }
                if (codeType == METHOD_CALL_NUM && blockCounter == 0) {
                    throw new IllegalLineException();
                }
                if (codeType == VARIABLE_ASSIGNMENT_NUM && blockCounter == 0) {
                    if (!variableAssignmentChecker(line))
                        throw new VariableException();
                }

            }
            lines.add(line);
            lastLine = line;
        }
        if(blockCounter > 0)
            throw new BlockException();
        return lines;
    }


    /**
     * A helping function which deduces what type of line we're dealing with through the method of the LineReader
     * a line is either blank, a comment, a code or illegal.
     *
     * @return int.
     */
    static private int getLineType(String line) {
        if (line.matches("\\s*"))
            return BLANK; // if the line  is only full of whitespaces, returns BLANK.
        if (line.startsWith("//"))
            return COMMENT; // if the "//" appears, the line is a comment.
        p = Pattern.compile(ENDS_WITH);
        m = p.matcher(line);
        if(m.find())
            return CODE;
        return WRONG;
    }


    /**
     * a line which gets presumably a line of Code and deduces what type of code it is, such as a
     * Method declaration, Variable declaration etc. using a Switch type by parsing the line.
     *
     * @param line
     */
    static private int getCodeType(String line) {
        if (line.matches(VARIABLE_ASSIGNMENT))
            return VARIABLE_ASSIGNMENT_NUM;
        if (line.matches(INT_DECLARATION))
            return VARIABLE_DECLARATION_NUM;
        if (line.matches(STRING_DECLARATION))
            return VARIABLE_DECLARATION_NUM;
        if (line.matches(CHAR_DECLARATION))
            return VARIABLE_DECLARATION_NUM;
        if (line.matches(BOOLEAN_DECLARATION))
            return VARIABLE_DECLARATION_NUM;
        if (line.matches(DOUBLE_DECLARATION))
            return VARIABLE_DECLARATION_NUM;
        if (line.matches(METHOD_DECLARATION))
            return METHOD_DECLARATION_NUM;
        if (line.matches(RETURN))
            return RETURN_NUM;
        if (line.matches(IF_WHILE))
            return IF_WHILE_NUM;
        if (line.matches(CLOSING_CURLY_BRACKET))
            return CURLY_BRACKET_NUM;
        if (line.matches(METHOD_CALL))
            return METHOD_CALL_NUM;
        return WRONG; // none of the regex were matched which means the code is invalid.
    }


    /**
     * returns the type of the variable declaration
     */
    public static int getVariableDeclarationType(String line) {
        if (line.matches(INT_DECLARATION))
            return INT_DEC_NUM;
        if (line.matches(STRING_DECLARATION))
            return STRING_DEC_NUM;
        if (line.matches(CHAR_DECLARATION))
            return CHAR_DEC_NUM;
        if (line.matches(BOOLEAN_DECLARATION)) {
            return BOOLEAN_DEC_NUM;
        }
        // we've all types so it must be a double.
        return DOUBLE_DEC_NUM;
    }


    /**
     * A helping method which superficially looks over errors that can occur during the opening or closing of
     * a block for the firstScan.
     *
     * @return: an updated blockCounter.
     */
    private static int basicBlockChecker(int codeType, int blockCounter, String lastLine, String line)
            throws IllegalLineException, BlockException {
        if (codeType == METHOD_DECLARATION_NUM && blockCounter == 0) { // blockcounter = 0 means
            // global
            blockCounter++;
            if (!BlockFactory.createMethod(line, global))
                throw new BlockException(); // if method was created its added to the global array.
        } else if (codeType == METHOD_DECLARATION_NUM) {
            throw new BlockException();
        } else if (codeType == IF_WHILE_NUM && blockCounter > 0) {
            blockCounter++;
        } else if (codeType == IF_WHILE_NUM) {
            throw new BlockException();
        } else if (codeType == CURLY_BRACKET_NUM) {
            blockCounter--;
            if (blockCounter < 0)
                throw new IllegalLineException();
            if (blockCounter == 0)
                if (!lastLine.matches(RETURN))
                    throw new BlockException();
        }
        return blockCounter;
    }

    /**
     * A helping method which takes in a line (which is definitely a Method Declaration) and returns
     * a type, a name and all of the  variables and their values as a seperate group.
     *
     * @param: line
     * @return: ArrayList(type, group of names and values)
     */

    private static String[] parseVariables(String line, int variableType) {
        switch (variableType) {
            case INT_DEC_NUM:
                p = Pattern.compile(INT_DECLARATION);
                break;
            case DOUBLE_DEC_NUM:
                p = Pattern.compile(DOUBLE_DECLARATION);
                break;
            case STRING_DEC_NUM:
                p = Pattern.compile(STRING_DECLARATION);
                break;
            case CHAR_DEC_NUM:
                p = Pattern.compile(CHAR_DECLARATION);
                break;
            case BOOLEAN_DEC_NUM:
                p = Pattern.compile(BOOLEAN_DECLARATION);
                break;
        }
        m = p.matcher(line);
        m.matches();
        String variableGroup = m.group(VARIABLE_GROUP);
        return variableGroup.split(",");
        // surely it wil be initialized as we only use this function in case of declaring a Variable.

    }


    /**
     * a method which gets a variable line and returns an array with the variable name in the first index and the
     * variable value  in the second index
     *
     * @param variableLine
     */
    private static String[] getNameAndValue(String variableLine) {
        String[] returnArray = new String[ARRAY_LENGTH];
        Pattern p = Pattern.compile(VARIABLE_PARSER);
        Matcher m = p.matcher(variableLine);
        m.matches();
        returnArray[NAME] = m.group(GROUP_NAME);
        if (m.group(GROUP_VALUE).equals("")){
            returnArray[VALUE] = m.group(GROUP_VALUE);
        } else {
            returnArray[VALUE] = m.group(SECONDARY_GROUP_VALUE);
        }
        // if the value doesnt exist, an empty string is added as the value.
        // by using the groups we parse and return the  array.
        return returnArray;
    }

    /**
     * a function which checks if the line contains the word final as its' first parameter.
     *
     * @param line
     */
    public static boolean containsFinal(String line) {
        return line.matches(HAS_FINAL);
    }


    private static boolean variableAssignmentChecker(String line) {
        p = Pattern.compile(VARIABLE_PARSER);
        m = p.matcher(line);
        String firstVariable = m.group(GROUP_NAME);
        String value = m.group(GROUP_VALUE);
        if (!global.getVariable(firstVariable).isFinal()) {
            if (value.matches(VARIABLE_PARSER)) {
                if (global.localContains(firstVariable) && global.localContains(value)) {
                    if (Variable.assignable(Variable.getVariableType(firstVariable, global), (Variable
                            .getVariableType(value, global)))) {
                        return true;
                    }
                }
            } else if (global.getVariable(firstVariable).assignValue(value))
                return true;
        }
        return false;
    }


    /**
     * a method that creates variables through the Variable factory, also uses other  functions to parse
     * a line such as this correctly.
     *
     * @param line
     */
    private static void createGlobalVariables(String line) throws VariableException {
        int variableType = WRONG;
        variableType = getVariableDeclarationType(line);
        boolean isFinal = containsFinal(line); //*******************
        String[] parsedVariables = parseVariables(line, variableType);
        for (String item : parsedVariables) {
            String name;
            String value;
            String[] nameAndValueSetter = getNameAndValue(item);
            name = nameAndValueSetter[NAME];
            value = nameAndValueSetter[VALUE];
            if (!global.addLocalVariable(VariableFactory.createVariable(variableType, name, value, isFinal, global)
            )) {
                throw new VariableException();
            }
        }
    }

    public static void main(String[] args) {
//        String test = "boolean a = _, _a = 3.6;";
//        p = Pattern.compile(BOOLEAN_DECLARATION);
//        m = p.matcher(test);
//        System.out.printf(String.valueOf(m.matches()));

    }
}

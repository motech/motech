import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.Comment;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.properties.DoubleProperty;
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.properties.StringProperty;
import net.sourceforge.pmd.AbstractJavaRule;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;

public class CommentedOutCode extends AbstractJavaRule {

    private static final PropertyDescriptor TRESHOLD_DESCRIPTOR = new DoubleProperty("classificationThreshold",
        "Sensitivity of classifying the source fragment as a commented java code", 0.95, 1.0f);

    private static final PropertyDescriptor SKIP_DESCRIPTOR = new StringProperty("skipCheckSequence",
        "When placed at the beginning of the comment, this sequence skips checking.", "##", 1.1f);

    private static final PropertyDescriptor JAVADOC_DESCRIPTOR = new BooleanProperty("skipJavaDocs",
        "Skip checking within JavaDocs comments", true, 1.2f);

    private double probability;
    private double threshold;
    private String skipSequence;
    private String skipSequencePattern;
    private boolean skipJavaDocs;

    public CommentedOutCode() {
    }

    private static final Map<String,PropertyDescriptor> PROPERTY_DESCRIPTORS_BY_NAME = asFixedMap(
        new PropertyDescriptor[] { TRESHOLD_DESCRIPTOR, SKIP_DESCRIPTOR, JAVADOC_DESCRIPTOR }
    );

    protected Map<String,PropertyDescriptor> propertiesByName() {
        return PROPERTY_DESCRIPTORS_BY_NAME;
    }

    @Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {

        threshold = this.getDoubleProperty(TRESHOLD_DESCRIPTOR);
        skipSequence = this.getStringProperty(SKIP_DESCRIPTOR);
        skipJavaDocs = this.getBooleanProperty(JAVADOC_DESCRIPTOR);
        skipSequencePattern = "^[ \t]*(//|/\\*(\\*)*)[ \t]*" + skipSequence + ".*$";

        for (Comment comment : cUnit.getComments()) {
            if( isCode(comment) ) {
                String msg = comment.getBeginLine() == comment.getEndLine() ? "Line " + comment.getBeginLine() :
                    "Lines from " + comment.getBeginLine() + " to " + comment.getEndLine();
                addViolationWithMessage(data, cUnit,
                    this.getMessage() + " " + msg + " classified as commented out java code with a probability of " + String.format("%.2f", probability)
                            + ". (TIP: you can adjust classificationTreshold property (actual is " + String.format("%.2f", threshold) + ") or use skipCheckSequence (actual is " + skipSequence +")).");
            }
        }

        return super.visit(cUnit, data);
    }

    private boolean isCode(Comment comment) {

        String lines[] = comment.getImage().split("\n");
        double p[] = new double[5];

        // don't check javadocs
        if(skipJavaDocs && lines[0].matches("^[ \t]*/\\*\\*.*$")) {
            return false;
        }

        for (String line : lines) {
            if(line.matches(skipSequencePattern)) {
                return false;
            }
            probability = 0.0;
            p[0] = 1 - Math.pow(1 - 0.95, (double)endsWithKeywords(line, "\\}", "\\;", "\\{"));
            probability = 1 - ((1 - probability) * (1 - p[0]));
            p[1] = 1 - Math.pow(1 - 0.7, (double)countKeywords(line, "\\|\\|", "\\&\\&"));
            probability = 1 - ((1 - probability) * (1 - p[1]));
            p[2] = 1 - Math.pow(1 - 0.1, (double)countKeywords(line, "public", "abstract", "class", "implements", "extends", "return", "throw",
                    "private", "protected", "enum", "continue", "assert", "package", "synchronized", "boolean", "this", "double",
                    "instanceof", "final", "interface", "static", "void", "long", "int", "float", "super", "true", "case\\:"));
            probability = 1 - ((1 - probability) * (1 - p[2]));
            p[3] = 1 - Math.pow(1 - 0.95, (double)containsKeywords(line, "\\+\\+", "for\\(", "if\\(", "while\\(", "catch\\(", "switch\\(", "try\\{", "else\\{"));
            probability = 1 - ((1 - probability) * (1 - p[3]));
            p[4] = 1 - Math.pow(1 - 0.5, (double)isCamelCase(line));
            probability = 1 - ((1 - probability) * (1 - p[4]));
            if(probability >= threshold) {
                return true;
            }
        }

        return false;
    }

    private int countKeywords(String line, String... keywords) {
        StringBuilder patternBuilder = new StringBuilder(keywords[0]);

        for(int i = 1; i<keywords.length; i++) {

            patternBuilder.append("|" + keywords[i]);
        }

        Pattern pattern = Pattern.compile(patternBuilder.toString());
        Matcher matcher = pattern.matcher(line);

        int count = 0;

        while(matcher.find()) {
            count++;
        }

        return count;
    }

    private int endsWithKeywords(String line, String... keywords) {
        StringBuilder patternBuilder = new StringBuilder(keywords[0]);

        for(int i = 1; i<keywords.length; i++) {

            patternBuilder.append("|" + keywords[i]);
        }

        Pattern pattern = Pattern.compile("(" + patternBuilder.toString() + ")$");
        Matcher matcher = pattern.matcher(line);

        return matcher.find() ? 1 : 0;
    }

    private int containsKeywords(String line, String... keywords) {
        return countKeywords(line.replaceAll("\\s", ""), keywords);
    }

    private int isCamelCase(String line) {
        char prevChar = ' ';
        char currChar;
        for (int i = 0; i < line.length(); i++) {
            currChar = line.charAt(i);
            if (Character.getType(prevChar) == Character.LOWERCASE_LETTER && Character.getType(currChar) == Character.UPPERCASE_LETTER) {
                return 1;
            }
            prevChar= currChar;
        }
        return 0;

    }
}

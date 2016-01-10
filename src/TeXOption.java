/**
 * Represents a LaTeX option for the TeXNotes Application.
 *
 * @author David Thomson
 * @version 1.0
 */
public class TeXOption implements Comparable<TeXOption> {

    private String name;
    private String code;
    private int order;

    /**
     * Constructs a blank TeXOption.
     */
    public TeXOption() {
        this.name = "";
        this.code = "";
        this.order = 0;
    }

    /**
     * Constructs a TeXOption with the given name, code, and order.
     *
     * @param name  The name of the option
     * @param code  The actual LaTeX code for the option
     * @param order The order in which the option will be added to the TeX file
     *              (mostly so that all documents created by TeXNotes will have
     *              the same format for package includes)
     */
    public TeXOption(String name, String code, int order) {
        this.name = name;
        this.code = code;
        this.order = order;
    }

    /**
     * @return The name of the option
     */
    public String getName() {
        return name;
    }

    /**
     * @return The actual LaTeX code for the option
     */
    public String getCode() {
        return code;
    }

    /**
     * @return The order in which the option will be added to the TeX file
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the name of the option.
     *
     * @param newName The new name for the option
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Sets the LaTeX code of the option.
     *
     * @param newCode The new code for the option
     */
    public void setCode(String newCode) {
        this.code = newCode;
    }

    /**
     * Sets the order in which the option will be added to the TeXFile
     *
     * @param newOrder The new order for the option
     */
    public void setOrder(int newOrder) {
        this.order = newOrder;
    }

    @Override
    public int compareTo(TeXOption other) {
        return this.getOrder() - other.getOrder();
    }
}

import java.util.Objects;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Region {

    @Property()
    private final String name;
    @Property()
    private final String currentElective;

    public String getName() {
        return name;
    }

    public String getCurrentElective() {
        return currentElective;
    }

    public Region(@JsonProperty("name") final String name, @JsonProperty("currentElective") final String currentElective) {
        this.name = name;
        this.currentElective = currentElective;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        Region other = (Region) obj;
        return Objects.deepEquals(new String[]{getName(), getCurrentElective()}, new String[]{other.getName(), other.getCurrentElective()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCurrentElective());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [name=" + name + ", currentElective=" + currentElective + "]";
    }
}
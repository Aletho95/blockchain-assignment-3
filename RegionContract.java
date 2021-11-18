import java.util.ArrayList;
import java.util.List;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import com.owlike.genson.Genson;

@Default
public final class RegionContract implements ContractInterface {

    private final Genson genson = new Genson();

    private enum RegionErrors {REGION_NOT_FOUND, REGION_ALREADY_EXISTS}

    /**
     * Retrieves a region with the specified key from the ledger.
     */
    @Transaction()
    public Region queryRegion(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String regionState = stub.getStringState(key);
        if (regionState.isEmpty()) {
            String errorMessage = String.format("Region %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, RegionErrors.REGION_NOT_FOUND.toString());
        }
        Region region = genson.deserialize(regionState, Region.class);
        return region;
    }

    /**
     * Creates some initial Regions on the ledger.
     */
    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        String[] regionData = {"{ \"name\": \"Syddanmark\", \"currentElective\": \"PoliticianUno\"}"};
        for (int i = 0; i < regionData.length; i++) {
            String key = String.format("REG%03d", i);
            Region region = genson.deserialize(regionData[i], Region.class);
            String regionState = genson.serialize(region);
            stub.putStringState(key, regionState);
        }
    }

    /**
     * Creates a new Region on the ledger.
     */
    @Transaction()
    public Region createRegion(final Context ctx, final String key, final String name, final String currentElective) {
        ChaincodeStub stub = ctx.getStub();
        String regionState = stub.getStringState(key);
        if (!regionState.isEmpty()) {
            String errorMessage = String.format("Region %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, RegionErrors.REGION_ALREADY_EXISTS.toString());
        }
        Region region = new Region(name, currentElective);
        regionState = genson.serialize(region);
        stub.putStringState(key, regionState);
        return region;
    }

    /**
     * Retrieves every region between REG0 and REG999 from the ledger.
     */
    @Transaction()
    public Region[] queryAllRegions(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        final String startKey = "REG0";
        final String endKey = "REG999";
        List<Region> regions = new ArrayList<Region>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);
        for (KeyValue result : results) {
            Region region = genson.deserialize(result.getStringValue(), Region.class);
            regions.add(region);
        }
        Region[] response = regions.toArray(new Region[regions.size()]);
        return response;
    }
}
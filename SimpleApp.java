import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleApp {

    public static void main(String[] args) throws Exception {
        //Create local path for a wallet that will manage identities
        Path walletPath = Paths.get("wallet");

        //Create the wallet
        Wallet wallet = Wallet.createFileSystemWallet(walletPath);

        //Create path for the network config
        Path networkConfigPath = Paths.get("..", "..", "assignment-3-network", "connection-org1.yaml");

        //Load a CCP from the tutorial example using the Gateway Builder
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(wallet, "alexander").networkConfig(networkConfigPath).discovery(true);

        //Initiate the gateway connection
        try (Gateway gateway = builder.connect()) {

            //Retrieve the network and contract (chaincode)
            Network network = gateway.getNetwork("channel1");
            Contract contract = network.getContract("simpleContract");

            byte[] result;

            //We query all the available regions to vote in and print the result
            result = contract.evaluateTransaction("queryAllRegions");
            System.out.println(new String(result));

            //We submit a transaction that create a new region, in this case "Hovedstaden"
            //and the politician who is currently in office.
            contract.submitTransaction("createRegion", "REG1" , "Hovedstaden", "Politician1");

            //We query the Hovedstaden region to see if we are presented with the correct info added above.
            result = contract.evaluateTransaction("queryRegion", "Hovedstaden");
            System.out.println(new String(result));
        }
    }
}

package xyx.game.madeinchina.show;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

import xyx.game.madeinchina.R;

public class ShowActivity extends AppCompatActivity {

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                textView.setText("Sorry cancell");
            } else {
                // Handle any other error codes.
                textView.setText("Sorry error");

            }
        }
    };
    private BillingClient billingClient;
    private TextView textView;

    void handlePurchase(Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchases or your PurchasesUpdatedListener.
       // Purchase purchase = purchase;

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    textView.setText("Thank you");

                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);


        Button bt=findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        billingClient = BillingClient.newBuilder(this)
               .setListener(purchasesUpdatedListener)
               .enablePendingPurchases()
               .build();

        // start();


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
              }
        });


    }

    private void start() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    textView.setText("START...");
                    List<String> skuList = new ArrayList<>();
                    skuList.add("vip1");
                    skuList.add("vip2");
                    skuList.add("vip5");
                    skuList.add("vip100");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    if (skuDetailsList.size()!=0) {

                                        for (SkuDetails sku:skuDetailsList){
                                           if (sku.getSku().equals("vip1")){
                                            // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                                            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                                    .setSkuDetails(sku)
                                                    .build();
                                            //textView.setText(skuDetailsList.get(0)+"\n" +skuDetailsList.get(1)+"\n"+ skuDetailsList.get(2)+"\n"+ skuDetailsList.get(3)+"\n");
                                            int responseCode = billingClient.launchBillingFlow(ShowActivity.this, billingFlowParams).getResponseCode();

                                        }
                                        }
                                         }
// Handle the result.
                                }
                            });
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });

    }
}

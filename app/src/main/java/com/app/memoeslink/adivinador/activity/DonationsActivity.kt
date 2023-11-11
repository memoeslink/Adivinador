package com.app.memoeslink.adivinador.activity

import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import com.app.memoeslink.adivinador.R
import games.moisoni.google_iab.BillingConnector
import games.moisoni.google_iab.BillingEventListener
import games.moisoni.google_iab.enums.ErrorType
import games.moisoni.google_iab.enums.ProductType
import games.moisoni.google_iab.models.BillingResponse
import games.moisoni.google_iab.models.ProductInfo
import games.moisoni.google_iab.models.PurchaseInfo


class DonationsActivity : CommonActivity() {
    private var btDonate: Button? = null
    private var btBack: Button? = null
    private var spnDonation: AppCompatSpinner? = null
    private var bc: BillingConnector? = null
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donations)
        btBack = findViewById(R.id.donations_back_button)
        btDonate = findViewById(R.id.donations_donate_button)
        spnDonation = findViewById(R.id.donations_spinner)

        // Initialize billing library
        bc = BillingConnector(this@DonationsActivity, GOOGLE_PUBLIC_KEY)
            .setConsumableIds(GOOGLE_CATALOG.toMutableList())
            .autoAcknowledge()
            .autoConsume()
            .enableLogging()
            .connect()

        val builder = AlertDialog.Builder(this@DonationsActivity)
        builder.setTitle(getString(R.string.alert_successful_donation_title))
        builder.setMessage(getString(R.string.alert_successful_donation_message))
        builder.setIcon(R.drawable.money)
        builder.setNeutralButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        dialog = builder.create()

        // Set adapters
        val adapter = ArrayAdapter(
            this@DonationsActivity,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.donation_amount)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnDonation?.adapter = adapter

        // Set listeners
        bc?.setBillingEventListener(object : BillingEventListener {
            override fun onProductsFetched(productDetails: MutableList<ProductInfo>) {
            }

            override fun onPurchasedProductsFetched(
                productType: ProductType,
                purchases: MutableList<PurchaseInfo>
            ) {
            }

            override fun onProductsPurchased(purchases: MutableList<PurchaseInfo>) {
                dialog?.show()
            }

            override fun onPurchaseAcknowledged(purchase: PurchaseInfo) {
            }

            override fun onPurchaseConsumed(purchase: PurchaseInfo) {
            }

            override fun onBillingError(
                billingConnector: BillingConnector,
                response: BillingResponse
            ) {
                when (response.errorType) {
                    ErrorType.CLIENT_NOT_READY -> {}
                    ErrorType.CLIENT_DISCONNECTED -> {}
                    ErrorType.CONSUME_ERROR -> {}
                    ErrorType.ACKNOWLEDGE_ERROR -> {}
                    ErrorType.ACKNOWLEDGE_WARNING -> {}
                    ErrorType.FETCH_PURCHASED_PRODUCTS_ERROR -> {}
                    ErrorType.BILLING_ERROR -> {}
                    ErrorType.USER_CANCELED -> {}
                    ErrorType.SERVICE_UNAVAILABLE -> {}
                    ErrorType.BILLING_UNAVAILABLE -> {}
                    ErrorType.ITEM_UNAVAILABLE -> {}
                    ErrorType.DEVELOPER_ERROR -> {}
                    ErrorType.ERROR -> {}
                    ErrorType.ITEM_ALREADY_OWNED -> {}
                    ErrorType.ITEM_NOT_OWNED -> {}
                    else -> {}
                }
            }
        })

        btDonate?.setOnClickListener {
            spnDonation?.selectedItemPosition?.let { index ->
                bc?.purchase(this@DonationsActivity, GOOGLE_CATALOG[index])
            }
        }

        btBack?.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        bc?.release()
    }

    companion object {
        private const val GOOGLE_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqg/e0wWLPo4Dd2+QGQhsasrOHBa8B6eTZuyiIVuzGjbOLzl8DHl5cqFrlN5FwoXlMBEBDIoIHkYt0f+GNxXqRw7K1BPWGZ0owPgKjAKJgQXq0iaImcdP7drmhxgDq8dwjQuRUd7UFu81gCXel3nQa9n6x2SQUZm+Of7/yYUtsDGp6AtM0BXEEdLegyDJ9RSCKWrUC+V6ZX103X+1iy73p/Sha2ntDaIBE8UV5doXDQYvppgyTuRr08Y4d+HKv5fvJKz3s13FEYeFV5euKyUZYV9INaYdIHPX/1S9CU1N9O/n/lTryJHGOa3rdAbShd93hAAyBIFfUrJOljiZhoFt1wIDAQAB"
        private val GOOGLE_CATALOG = mutableListOf(
            "donation.10",
            "donation.20",
            "donation.50",
            "donation.100",
            "donation.200",
            "donation.500",
            "donation.1000"
        )
    }
}
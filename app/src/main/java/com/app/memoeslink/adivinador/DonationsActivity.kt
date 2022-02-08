package com.app.memoeslink.adivinador

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import org.sufficientlysecure.donations.BuildConfig
import org.sufficientlysecure.donations.DonationsFragment

class DonationsActivity : CommonActivity() {
    private var btBack: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donations)
        btBack = findViewById(R.id.donations_back_button)

        //Define Fragment for donations
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val donationsFragment = DonationsFragment.newInstance(
                BuildConfig.DEBUG,
                true,
                GOOGLE_PUBLIC_KEY,
                GOOGLE_CATALOG,
                resources.getStringArray(R.array.donation_amounts),
                false,
                null,
                null,
                null,
                false,
                null,
                null,
                false,
                null
        )
        fragmentTransaction.replace(
                R.id.donations_container,
                donationsFragment,
                "donationsFragment"
        )
        fragmentTransaction.commit()

        //Set listeners
        btBack?.setOnClickListener { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag("donationsFragment")
        fragment?.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val GOOGLE_PUBLIC_KEY =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqg/e0wWLPo4Dd2+QGQhsasrOHBa8B6eTZuyiIVuzGjbOLzl8DHl5cqFrlN5FwoXlMBEBDIoIHkYt0f+GNxXqRw7K1BPWGZ0owPgKjAKJgQXq0iaImcdP7drmhxgDq8dwjQuRUd7UFu81gCXel3nQa9n6x2SQUZm+Of7/yYUtsDGp6AtM0BXEEdLegyDJ9RSCKWrUC+V6ZX103X+1iy73p/Sha2ntDaIBE8UV5doXDQYvppgyTuRr08Y4d+HKv5fvJKz3s13FEYeFV5euKyUZYV9INaYdIHPX/1S9CU1N9O/n/lTryJHGOa3rdAbShd93hAAyBIFfUrJOljiZhoFt1wIDAQAB"
        private val GOOGLE_CATALOG = arrayOf(
                "donation.10",
                "donation.20",
                "donation.50",
                "donation.100",
                "donation.200",
                "donation.500"
        )
    }
}
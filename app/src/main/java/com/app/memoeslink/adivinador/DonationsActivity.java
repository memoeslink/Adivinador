package com.app.memoeslink.adivinador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.sufficientlysecure.donations.BuildConfig;
import org.sufficientlysecure.donations.DonationsFragment;

public class DonationsActivity extends CommonActivity {
    private static final String GOOGLE_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqg/e0wWLPo4Dd2+QGQhsasrOHBa8B6eTZuyiIVuzGjbOLzl8DHl5cqFrlN5FwoXlMBEBDIoIHkYt0f+GNxXqRw7K1BPWGZ0owPgKjAKJgQXq0iaImcdP7drmhxgDq8dwjQuRUd7UFu81gCXel3nQa9n6x2SQUZm+Of7/yYUtsDGp6AtM0BXEEdLegyDJ9RSCKWrUC+V6ZX103X+1iy73p/Sha2ntDaIBE8UV5doXDQYvppgyTuRr08Y4d+HKv5fvJKz3s13FEYeFV5euKyUZYV9INaYdIHPX/1S9CU1N9O/n/lTryJHGOa3rdAbShd93hAAyBIFfUrJOljiZhoFt1wIDAQAB";
    private static final String[] GOOGLE_CATALOG = new String[]{"donation.10", "donation.20", "donation.50", "donation.100", "donation.200", "donation.500"};
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donations);
        button = findViewById(R.id.donations_back_button);

        //Define Fragment for donations
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DonationsFragment donationsFragment = DonationsFragment.newInstance(BuildConfig.DEBUG, true, GOOGLE_PUBLIC_KEY, GOOGLE_CATALOG, getResources().getStringArray(R.array.donation_values), false, null, null, null, false, null, null, false, null);
        fragmentTransaction.replace(R.id.donations_container, donationsFragment, "donationsFragment");
        fragmentTransaction.commit();

        //Set listeners
        button.setOnClickListener(view -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("donationsFragment");

        if (fragment != null)
            fragment.onActivityResult(requestCode, resultCode, data);
    }
}

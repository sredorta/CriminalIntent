package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sredorta on 9/19/2016.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateNoCrimes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNoCrimes();
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    //Updates text of noCrimes if no crimes exists
    private void updateNoCrimes() {
        final TextView mTitleNoCrimes =  (TextView) findViewById(R.id.textViewNoCrimes);

        CrimeLab crimeLab = CrimeLab.get(this);
        List<Crime> crimes = crimeLab.getCrimes();
        if (crimes.size()>0) {
            mTitleNoCrimes.setVisibility(View.INVISIBLE);
        } else {
            mTitleNoCrimes.setVisibility(View.VISIBLE);
        }
    }

}

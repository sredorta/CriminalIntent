package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bignerdranch.android.criminalintent.database.CrimeDbSchema;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.UUID;

/**
 * Created by sredorta on 9/19/2016.
 */
public class CrimeFragment extends Fragment {
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_PHOTO = " DialogPhoto";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int CALL_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int SHOW_PHOTO = 4;



    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;


    //Method that is like a constructor but adds in the bundle an argument
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Make sure that onCreateOptiopnsMenu is called to delpoy ActionBar
        setHasOptionsMenu(true);
        //mCrime = new Crime();
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    //Define action bar when only one crime is visible
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        MenuItem removeItem = menu.findItem(R.id.menu_item_remove_crime);
        MenuItem addItem = menu.findItem(R.id.menu_item_new_crime);
        //Do not show the removeItem as visible, only when one Crime is selected
        removeItem.setVisible(true);
        addItem.setVisible(false);
        subtitleItem.setVisible(false);

    }

    //Answer ActionBar menu selection when remove is done
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                return true;
            case R.id.menu_item_show_subtitle:
                return true;
            case R.id.menu_item_remove_crime:
                //Toast.makeText(getActivity(), "Clicked remove!", Toast.LENGTH_SHORT ).show();
                CrimeLab.get(getActivity()).removeCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //Handle date button
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        UpdateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        //Handle checkbox
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        //Handle send report handler
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setType("text/plain")
                        .getIntent();

                //  Intent i = new Intent(Intent.ACTION_SEND);
                //  i.setType("text/plain");
                //  i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                //  i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                //  i = Intent.createChooser(i,getString(R.string.send_report));
                startActivity(i);
            }
        });
        //Handle choose suspect among contacts
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        //To verify that when no Intent available button is disabled
        //pickContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }
        //Check if contacts are available, if not disable button
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        //Handle Phone call
        ImageButton crimePhoneCall = (ImageButton) v.findViewById(R.id.crime_phone_call);
        //Find phone number of the suspect
//        Toast.makeText(getActivity(),"Suspect is:" + mCrime.getSuspect(), Toast.LENGTH_LONG).show();
        crimePhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri callNumberUri = findContactNumber(mCrime.getSuspect());
                final Intent callContact = new Intent(
                        Intent.ACTION_DIAL,callNumberUri);
                if (callNumberUri != null) {
                    startActivity(callContact);
                }
            }
        });
        //Handle photos button
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView   = (ImageView)   v.findViewById(R.id.crime_photo);
        updatePhotoView();

        return v;
    }
    private Uri findContactNumber(String suspect) {
        //If suspect not defined
        if (suspect == null) {
            Toast.makeText(getActivity(), R.string.crime_call_no_suspect, Toast.LENGTH_LONG).show();
            return null;
        } else {
            //Find number from the suspect
            Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            //query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
 /*           Cursor cursor = mDatabase.query(CrimeDbSchema.CrimeTable.NAME,
                    null, //Columns - null select all Columns
                    whereClause,
                    whereArgs,
                    null, // groupBy
                    null, // having
                    null);// orderBy
*/
            String[] queryFields = new String[]{
                    ContactsContract.CommonDataKinds.Phone._ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
            };
            String whereClause = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "= ?";
            String[] argsWhere = {mCrime.getSuspect()};

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, whereClause, argsWhere, null);
            if (c.getCount() == 0) {
                String noPhoneDefined = getString(R.string.crime_call_no_phone, mCrime.getSuspect());
                Toast.makeText(getActivity(), noPhoneDefined, Toast.LENGTH_LONG).show();
                c.close();
                return null;
            } else {
                c.moveToFirst();
                //The second index defines the number as defined in the query
                String numberp = c.getString(2);
                Uri number = Uri.parse("tel:" + numberp);
                return number;
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            UpdateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            //Specify in which field we want values
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //Make the query, contactUri is like WHERE in SQLite
            Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            //Check if results available
            try {
              if (c.getCount() == 0) {
                  return;
              }
              c.moveToFirst();
              String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        } else if (requestCode == CALL_CONTACT && data != null) {
            //Uri callUri = data.getData();
        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void UpdateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    // Creates a string with a report
    private String getCrimeReport() {
        String solvedString = null;

        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
    // Updates photoView with Scaled image
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageResource(R.drawable.ic_menu_camera);
            mPhotoView.setColorFilter(Color.RED);
        } else {
            mPhotoView.clearColorFilter();
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
            //Add onClock listener to open a Dialog with the larger image
            mPhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    PhotoFragment dialog = PhotoFragment.newInstance(mPhotoFile);
                    dialog.setTargetFragment(CrimeFragment.this, SHOW_PHOTO);
                    dialog.show(manager, DIALOG_PHOTO);
                }
            });
        }

    }
}

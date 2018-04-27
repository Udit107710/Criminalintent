package com.example.user.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
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
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by user on 27-12-2017.
 */

public class CrimeFragment extends Fragment {

    private Crime mCrime;
    private EditText mTitleText;
    private Button mDateButton;
    private Button mTImeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFIle;
    private Callbacks mCallbacks;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DIalogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PERMISSION_CALL = 3;
    private static final int REQUEST_PHOTO = 4;
    //private static final String TAG = "myApp";

    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFIle = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_menu:
                DeleteCrime();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void DeleteCrime() {
        CrimeLab.get(getActivity()).removeCrime(mCrime);
    }
    public void DeleteCrime(Crime crime){
        CrimeLab.get(getActivity()).removeCrime(crime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if( getContext().checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PERMISSION_CALL);

        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleText = (EditText) v.findViewById(R.id.crime_title);
        mTitleText.setText(mCrime.getTitle());
        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mDateButton = v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTImeButton = v.findViewById(R.id.crime_time);
        updateTime();
        mTImeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(Intent.ACTION_SEND);
                //i.setType("text/plain");
                //i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                //i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                //i = Intent.createChooser(i, getString(R.string.send_report));

                Intent i = ShareCompat.IntentBuilder.from(getActivity()).setChooserTitle(getString(R.string.send_report)).setType("text/plain").setText(getCrimeReport()).setSubject(getString(R.string.crime_report_subject)).createChooserIntent();

                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();


        mCallButton = (Button) v.findViewById(R.id.call_suspect);

        final Intent callContact = new Intent(Intent.ACTION_DIAL);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = "tel:" + mCrime.getNumber();
                callContact.setData(Uri.parse(tel));
                startActivity(callContact);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
            mCallButton.setText(getString(R.string.call_suspect,mCrime.getSuspect()));
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePicture = mPhotoFIle !=null && captureImage.resolveActivity(packageManager) != null ;
        mPhotoButton.setEnabled(canTakePicture);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.user.criminalintent.fileprovider",mPhotoFIle);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage,REQUEST_PHOTO);
            }

        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoFIle == null || !mPhotoFIle.exists()) return;

                FragmentManager fm = getActivity().getSupportFragmentManager();
                PhotoDialog photoDialogFragment = PhotoDialog.newInstance(mPhotoFIle.getPath());
                photoDialogFragment.show(fm, "daialog_photo");
            }
        });

        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
        if (packageManager.resolveActivity(callContact, PackageManager.MATCH_DEFAULT_ONLY) == null){
            mCallButton.setEnabled(false);
        }

        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) intent.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
            updateTime();
            updateCrime();
        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) intent.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);
            updateDate();
            updateTime();
            updateCrime();
        } else if (requestCode == REQUEST_CONTACT && intent != null) {
            Uri contactUri = intent.getData();
            Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null);

            try {
                if (cursor.getCount() == 0) {
                    return;
                }

                cursor.moveToFirst();
                String suspect = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                if (Integer.parseInt(cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    mCallButton.setEnabled(true);
                    getSuspectPhone(id);
                    mCallButton.setText(getString(R.string.call_suspect, suspect));
                } else mCallButton.setEnabled(false);
            } finally {
                cursor.close();
            }

            updateCrime();
        }
        else if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.user.criminalintent.fileprovider",mPhotoFIle);

            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }

    }

    private void updateCrime(){
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private void updateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCrime.getDate());
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        mTImeButton.setText(hour + ":" + minute);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect,mCrime.getSuspect());
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void getSuspectPhone(String id) {
                Cursor cursor = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?" ,
                        new String[] {id}, null);
        if (cursor.moveToFirst()) {
            mCrime.setNumber(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            //Log.v(TAG, mCrime.getNumber());
            //mCrime = CrimeLab.get(getActivity()).getCrime(mCrime.getId());
        }
        cursor.close();
        CrimeLab.get(getActivity()).updateCrime(mCrime);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void updatePhotoView(){
        if (mPhotoFIle == null || !mPhotoFIle.exists()) mPhotoView.setImageDrawable(null);
        else {
            Bitmap bitmap = PictureUtil.getScaledBitmap(mPhotoFIle.getPath(),getActivity());

            mPhotoView.setImageBitmap(bitmap);
            updateCrime();
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }
    
}
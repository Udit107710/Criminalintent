package com.example.user.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import javax.security.auth.callback.Callback;

import static com.example.user.criminalintent.CrimeLab.*;

/**
 * Created by user on 30-12-2017.
 */

public class CrimeListFragment extends Fragment {

    private static String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private static int mPosition;
    private boolean mSubtitleVisibile;
    private TextView mEmptyTextView;
    private Button mEmptyButton;
    private Callbacks mCallbacks;

    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }

    @Override                                       //THE HOSTING ACTIVITY MUST IMPLEMENT THIS FUCNTION
    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanState){
        super.onCreate(savedInstanState);
        setHasOptionsMenu(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_crime :
                                    Crime crime = new Crime();
                                    get(getActivity()).addCrime(crime);
                                    updateUI();
                                    mCallbacks.onCrimeSelected(crime);
                                    return true;
            case R.id.show_subtitle:
                                    mSubtitleVisibile = !mSubtitleVisibile;
                                    getActivity().invalidateOptionsMenu();
                                    updateSubtitle();
                                    return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        int size = get(getActivity()).getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural,size,size);

        if (!mSubtitleVisibile) subtitle = null;

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances){
        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);
        mEmptyButton = (Button) view.findViewById(R.id.empy_button);
        mEmptyTextView = (TextView) view.findViewById(R.id.empty_text);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstances !=null){
            mSubtitleVisibile = savedInstances.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        ItemTouchHelper.SimpleCallback simpleCallback = new SwipeHelper(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(mCrimeRecyclerView);

        updateUI();
        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisibile) subtitleItem.setTitle(R.string.hide_subtitle);
        else subtitleItem.setTitle(R.string.show_subtitle);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisibile);
    }


    public void updateUI(){
        CrimeLab crimeLab = get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(crimes.size()==0){
            mEmptyButton.setVisibility(View.VISIBLE);
            mEmptyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newCrime();
                }
            });
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyTextView.setVisibility(View.GONE);
            mEmptyButton.setVisibility(View.GONE);
        }


        if(mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
            mAdapter.setCrimes(crimes);
            mAdapter.notifyItemChanged(mPosition);
        }

        updateSubtitle();
    }

    private void newCrime() {
        Crime crime = new Crime();
        get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
        startActivity(intent);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;
        private ImageView mImageView;



        public CrimeHolder(LayoutInflater inflater,ViewGroup parent){

            super(inflater.inflate(R.layout.list_item_crime,parent,false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mImageView = (ImageView) itemView.findViewById(R.id.crime_solved);


        }

        public void bind(Crime crime ){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);

        }

        @Override
        public void  onClick(View view){
            mPosition = getAdapterPosition();
            Toast.makeText(getActivity(),mCrime.getTitle(),Toast.LENGTH_SHORT).show();

           mCallbacks.onCrimeSelected(mCrime);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);

        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }

        public  void dismissCrime(int pos){
            mCrimes.remove(pos);
            this.notifyItemRemoved(pos);
            //CrimeLab.removeCrime(mCrimes.get(pos));
        }

    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

    private class SwipeHelper extends ItemTouchHelper.SimpleCallback{

        CrimeAdapter mAdapter;

        public SwipeHelper(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        public SwipeHelper( CrimeAdapter adapter) {
            super(ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT);
            mAdapter = adapter;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            mAdapter.dismissCrime(viewHolder.getAdapterPosition());

        }
    }



}

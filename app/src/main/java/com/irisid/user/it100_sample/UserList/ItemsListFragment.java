package com.irisid.user.it100_sample.UserList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.irisid.it100.IT100;
import com.irisid.it100.callback.AllUserCallback;
import com.irisid.it100.callback.AllUserCountCallback;
import com.irisid.it100.data.MessageKeyValue;
import com.irisid.it100.data.MessageType;
import com.irisid.it100.data.UserSimpleInfo;
import com.irisid.user.it100_sample.Common.util.Logger;
import com.irisid.user.it100_sample.IrisApplication;
import com.irisid.user.it100_sample_project.R;

import java.util.ArrayList;

public class ItemsListFragment extends ListFragment {

    static private ListViewAdapter listViewAdapter;
    static public ArrayList<Item> itemArrayList = new ArrayList<Item>();

    public interface OnItemSelectedListener {
        void onItemSelected(Item i, boolean isNew);
    }

    private OnItemSelectedListener listener;
    private SearchView search_edt;
    private Button enroll_fab;
    EditText editText;
    LinearLayout linearTotalUser;
    TextView txtTotalUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ImageView searchClose;
    View footer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_items_list, container,false);
        linearTotalUser = view.findViewById(R.id.linear_total_count);
        txtTotalUser = view.findViewById(R.id.txt_total_user);

        search_edt = (SearchView) view.findViewById(R.id.search_edt);
        int id = search_edt.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);

        int img_id = search_edt.getContext()
                .getResources()
                .getIdentifier("android:id/search_close_btn", null, null);

        searchClose = (ImageView)search_edt.findViewById(img_id);
        searchClose.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);


        search_edt.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals(""))
                    linearTotalUser.setVisibility(View.VISIBLE);
                else
                    linearTotalUser.setVisibility(View.GONE);

                getSearchUserList(newText, 0, 100, false);
                getUserCount();
                return false;
            }
        });
        search_edt.setQuery("", false);
        search_edt.setIconified(true);

        editText = (EditText)search_edt.findViewById(id);
        editText.setHintTextColor(getResources().getColor(R.color.muteText, null));
        editText.setTextColor(getResources().getColor(R.color.white, null));
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    // Your piece of code on keyboard search click
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    search_edt.clearFocus();
                    searchClose.setVisibility(View.GONE);

                    return true;
                }
                return false;
            }
        });

        enroll_fab = (Button)view.findViewById(R.id.enroll_fab);
        enroll_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(((Item)itemArrayList.get(itemArrayList.size()-1)).isNew)
                    return;
                Item item = new Item();
                item.isNew = true;
                //item.setIs_admin(MessageKeyValue.USER_ROLE_USER);
                //item.setUser_guid("999");
                item.setFirst_name("New User");
                item.setLast_name("");
                item.userConsent = false;
                itemArrayList.add(item);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listViewAdapter.set_selected_position(itemArrayList.size()-1);
                        if(listViewAdapter!=null) {
                            listViewAdapter.notifyDataSetChanged();
                        } else{
                            listViewAdapter = new ListViewAdapter(getActivity(), itemArrayList);
                            setListAdapter(listViewAdapter);
                        }
                    }
                });
                if (listener != null) {
                    listener.onItemSelected(item, true);
                }
//                Item item = new Item();
//                listViewAdapter.get
//                listViewAdapter.set_selected_position(position);
//                listViewAdapter.notifyDataSetChanged();
//
//                if (listener != null) {
//                    listener.onItemSelected(item);
//                }
//                Intent intent = new Intent(getActivity(), CaptureActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra(ConstData.CAPTURE_STATE, ConstData.CAPTURE_STATE_ENROLL_USER);
//                startActivityForResult(intent, IrisApplication.CaptureActivityValue);
            }
        });

        footer = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.listview_footer, null, false);

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

       // Logger.d("@onListItemClick  position is "+ position + " / "+ itemArrayList.size());
        if(position== itemArrayList.size()) {
            getSearchUserList(editText.getText().toString(), position, 100, true);
        }else {
            Item item = listViewAdapter.getListItem(position);
            listViewAdapter.set_selected_position(position);
            listViewAdapter.notifyDataSetChanged();

            if (listener != null) {
                if(item.isNew) {
                    //listener.onItemSelected(item, true);
                }else {
                    listener.onItemSelected(item, false);
                    for(int i =0 ; i<itemArrayList.size(); i++){
                        Item tempItem = itemArrayList.get(i);
                        if(tempItem.isNew==true) {
                            itemArrayList.remove(i);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listViewAdapter = new ListViewAdapter(getActivity(), itemArrayList);
        setListAdapter(listViewAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ItemsListFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void listItemUpdate() {
        if(listViewAdapter!=null) {
            listViewAdapter.notifyDataSetChanged();
        } else{
            listViewAdapter = new ListViewAdapter(getActivity(), itemArrayList);
            setListAdapter(listViewAdapter);
        }
        listViewAdapter.set_selected_position(-1);
    }

    public void clearFocus(){
        if(listViewAdapter!=null) {
            listViewAdapter.set_selected_position(-1);
            listViewAdapter.notifyDataSetChanged();
        }
    }

    public void deleteNewUser() {

        for(int i =0 ; i<itemArrayList.size(); i++){
            Item item = itemArrayList.get(i);

            if(item.isNew==true) {

                Logger.d("deleteNewUser index is "+i);
                itemArrayList.remove(i);
            }
        }

        if(listViewAdapter!=null) {
            listViewAdapter.set_selected_position(-1);
            listViewAdapter.notifyDataSetChanged();
        }
    }
    public void getSearchUserList(final String searchText, final int from, final int limit, final boolean isAdd){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                long rtnUserList = 0 ;
                //rtnUserList = IT100.getUserList(searchText, from, limit, new AllUserCallback() {
                String sText = searchText;
                if(editText!=null)
                    sText = editText.getText().toString();
                rtnUserList = IT100.getUserList(sText, from, limit, new AllUserCallback() {

                    @Override
                    public void allUserResult(final ArrayList<UserSimpleInfo> arrayList) {
                        if(arrayList!=null){
                            UserSimpleInfo tempUser;
                            if(!isAdd)
                                itemArrayList.clear();

                            for (int i=0; i<arrayList.size(); i++){
                                tempUser = arrayList.get(i);
                                Item item = new Item();
                                item.setUser_id(tempUser.userID);
                                item.setFirst_name(tempUser.firstName);
                                item.setLast_name(tempUser.lastName);
                                item.setFace_small_img(tempUser.faceimage);
                                item.setIs_admin(tempUser.role);
                                itemArrayList.add(item);

                                if(tempUser.role.equals(MessageKeyValue.USER_ROLE_ADMINISTRATOR)) {
                                    if(tempUser.status.equals(MessageKeyValue.USER_ACTIVE))
                                        IrisApplication.isAdminActive = true;
                                    else
                                        IrisApplication.isAdminActive = false;
                                }
                            }

                            if(getActivity()!=null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if(arrayList.size()<100)
                                            getListView().removeFooterView(footer);
                                        else {
                                            if(getListView().getFooterViewsCount()<1)
                                                getListView().addFooterView(footer);
                                        }

                                        listItemUpdate();
                                    }
                                });
                            }
                        }else{
                            //Logger.d("arrayList is  null ");
                        }
                    }
                });

                if ( rtnUserList  == MessageType.ID_RTN_SUCCESS){   }// success
                else if( rtnUserList  == MessageType.ID_RTN_WRONG_PARA){}// fail
                else if( rtnUserList  == MessageType.ID_RTN_NOT_OPENED_FAIL){}// fail
                else if( rtnUserList  == MessageType.ID_RTN_FAIL){}// fail
            }
        });
    }

    public void getUserCount(){
        IT100.getAllUserCount(new AllUserCountCallback() {
            @Override
            public void onResult(final int count) {
               // Logger.d("User total number is "+ count);
                if(getActivity()!=null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtTotalUser.setText(String.valueOf(count));
                        }
                    });
                }
            }
        });

    }
}
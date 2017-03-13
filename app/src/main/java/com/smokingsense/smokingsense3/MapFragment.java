package com.smokingsense.smokingsense3;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by appaaaa on 2017-02-02.
 */

public class MapFragment extends Fragment {
    public MapFragment(){

    }



    //NAVER MAP API
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "NMapFragment";

    NMapView mapView;
    private NMapContext mMapContext;
    private static final String CLIENT_ID = "QOWzHTDskgbKWIZ9CIFq";
    private NMapController mMapController; // 지도 상태 변경 컨트롤. 지도 중심 및 축적 레벨 변경, 지도 확대, 축소, 패닝 등 다양한 기능 수행

    private NMapLocationManager mMapLocationManager; // 단말기의 현재 위치 탐색 기능을 사용하기 위한 클래스입니다. 내부적으로 시스템에서 제공하는 GPS및 네트워크를 모두 사용하여 현재 위치를 탐색합니다.
    private NMapMyLocationOverlay mMyLocationOverlay; //지도 위에 현재 위치를 표시하는 오버레이 클래스이며 NMapOverlay 클래스를 상속합니다.
    private NMapCompassManager mMapCompassManager; //단말기의 나침반 기능을 사용하기 위한 클래스입니다.

    private NMapOverlayManager mOverlayManager; //지도 위에 표시되는 오버레이 객체를 관리합니다.

    private NMapViewerResourceProvider mMapViewerResourceProvider; // 지도 위의 오버레이 객체 드로잉에 필요한 리소스 데이터를 제공하기 위한 추상 클래스입니다. 본 클래스를 상속받은 객체를 NMapOverlayManager 객체 생성 시 인자로 전달해야 합니다.



    private SharedPreferences mPreferences;
    private static boolean mIsMapEnlared = false;

    private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(126.978371, 37.5666091);
    private static final int NMAP_ZOOMLEVEL_DEFAULT = 13;
    private static final int NMAP_VIEW_MODE_DEFAULT = NMapView.VIEW_MODE_VECTOR;

    private static final String KEY_ZOOM_LEVEL = "MapFragment.zoomLevel";
    private static final String KEY_CENTER_LONGITUDE = "NMapViewer.centerLongitudeE6";
    private static final String KEY_CENTER_LATITUDE = "NMapViewer.centerLatitudeE6";
    private static final String KEY_VIEW_MODE = "NMapViewer.viewMode";

    // Firebase Realtime Databse
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mCafeDatabaseReference;
    private ChildEventListener mChildEventListener;

    //Test firebase search
    private DatabaseReference searcHRef;

    //Test ArrayList for search
    private ArrayList<SmokingLocation> myList;
    private ArrayList<SmokingLocation> myList2;


    ListView mListView;
    SmokingLocationAdapter mAdapter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapContext = new NMapContext(super.getActivity());
        mMapContext.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mapfragment, container, false);

        //List<SmokingLocation> dataList = new ArrayList<>();
        myList = new ArrayList<SmokingLocation>();

        mListView = (ListView)rootView.findViewById(R.id.listview1);
        mAdapter = new SmokingLocationAdapter(getActivity(), R.layout.list_item1, myList);
        mListView.setAdapter(mAdapter);

        //list for search. copy list
        myList2 = new ArrayList<SmokingLocation>();


        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mapView = (NMapView)getView().findViewById(R.id.mapView);
        mapView.setClientId(CLIENT_ID);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setFocusable(true);
        mapView.setFocusableInTouchMode(true);
        mMapContext.setupMapView(mapView);

        mapView.setOnMapStateChangeListener(onMapViewStateChangeListener);

        mMapController = mapView.getMapController();

        mMapViewerResourceProvider = new NMapViewerResourceProvider(getActivity());

        mOverlayManager = new NMapOverlayManager(getActivity(), mapView, mMapViewerResourceProvider);

        mMapLocationManager = new NMapLocationManager(getActivity());
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

        mMapCompassManager = new NMapCompassManager(getActivity());
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);





        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                startMyLocation();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

            }
        };
        new TedPermission(getActivity())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("지도 서비스를 사용하기 위해서는 위치 접근 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 위치 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        //Firebase Realtime Databse;
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mCafeDatabaseReference = mFirebaseDatabse.getReference().child("cafes");

        // Test Search Firebase
        searcHRef = mFirebaseDatabse.getReference("cafes");

        attachDatabaseReadListener();



    }


    private void attachDatabaseReadListener(){
        if(mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    SmokingLocation mSmokingLocation = dataSnapshot.getValue(SmokingLocation.class);

                    int markerId = NMapPOIflagType.PIN;

                    NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
                    poiData.beginPOIdata(1);
                    poiData.addPOIitem(mSmokingLocation.getLongitude(), mSmokingLocation.getLatitude(), mSmokingLocation.getTitle().toString(), markerId, 0);
                    poiData.endPOIdata();

                    NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
                    mAdapter.add(mSmokingLocation);
                    myList2.add(mSmokingLocation); //데이터 받아오기 : 정렬과 검색을 위해서.



                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mCafeDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void startMyLocation(){
        if(mMapLocationManager.isMyLocationEnabled()){
            if(!mapView.isAutoRotateEnabled()){

                mapView.setAutoRotateEnabled(true, false); //지도 회전 활성화 결정 (회전 활성화 여부, 회전 해제 시 애니메이션 여부)
                mapView.requestLayout();
            }else{
                //stopmylocation();
            }
            mapView.postInvalidate(); // 지도 내용 변경 되었으니 다시 그리기
        }else{
            boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
            if(!isMyLocationEnabled){
                Toast.makeText(getActivity(), "설정에서 위치정보허용 을 설정하십시오.", Toast.LENGTH_LONG).show();

                Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(goToSettings);

                return;
            }
        }
    }
    // 지도 위치 변경 알려주는 리스너
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener(){
        @Override
        public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
            if(mMapController != null){
                mMapController.animateTo(nGeoPoint); //지도 중심점을 전달된 좌표로 변경한다. 지도 중심점 이동 시 패닝 애니메이션이 수행된다.
            }
            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {

        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {

        }
    };

    private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener(){
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
            if(nMapError == null){ // success
                // restore map view state such as map center position and zoom level.
                restoreInstanceState();
            }
        }

        @Override
        public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
            }
        }

        @Override
        public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onMapCenterChange: center=" + center.toString());
            }
        }

        @Override
        public void onZoomLevelChange(NMapView mapView, int level) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onZoomLevelChange: level=" + level);
            }
        }

        @Override
        public void onMapCenterChangeFine(NMapView mapView) {

        }
    };

    private void restoreInstanceState() {
        mPreferences = getActivity().getPreferences(MODE_PRIVATE);

        int longitudeE6 = mPreferences.getInt(KEY_CENTER_LONGITUDE, NMAP_LOCATION_DEFAULT.getLongitudeE6());
        int latitudeE6 = mPreferences.getInt(KEY_CENTER_LATITUDE, NMAP_LOCATION_DEFAULT.getLatitudeE6());
        int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
        int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
       // boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
       // boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);

        mMapController.setMapViewMode(viewMode);
      //  mMapController.setMapViewTrafficMode(trafficMode);
       // mMapController.setMapViewBicycleMode(bicycleMode);
        mMapController.setMapCenter(new NGeoPoint(longitudeE6, latitudeE6), level);

        if (mIsMapEnlared) {
            mapView.setScalingFactor(2.0F);
        } else {
            mapView.setScalingFactor(1.0F);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


        getActivity().getMenuInflater().inflate(R.menu.search, menu);

        MenuItem searchMenu = menu.findItem(R.id.menu_search);
        SearchView mSearchView = (SearchView) searchMenu.getActionView();
        mSearchView.setQueryHint("흡연 장소 검색");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {

                for(int i= 0; i < myList.size(); i++){
                    Log.v("down data to list", myList.get(i).getTitle().toString());
                }
                Log.v("myList size : ", Integer.toString(myList.size()));

                mAdapter.clear();
           //     poiDataOverlay.removeAllPOIdata();

                for(int i= 0; i < myList2.size(); i++){
                    if(myList2.get(i).getTitle().toString().contains(query)) {
                        int markerId = NMapPOIflagType.PIN;

                        NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
                        poiData.beginPOIdata(1);
                        poiData.addPOIitem(myList2.get(i).getLongitude(), myList2.get(i).getLatitude(), myList2.get(i).getTitle().toString(), markerId, 0);
                        poiData.endPOIdata();


                        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

                        mAdapter.add(myList2.get(i));
                        Log.v("test search : " ,myList2.get(i).getTitle().toString());
                    }
                }


                //mAdapter.clear();

                //mChildEventListener.onChildRemoved();

                /*
                searcHRef.orderByChild("title").startAt(query).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        SmokingLocation mSmokingLocation = dataSnapshot.getValue(SmokingLocation.class);

                        int markerId = NMapPOIflagType.PIN;

                        NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
                        poiData.beginPOIdata(1);
                        NMapPOIitem item = poiData.addPOIitem(mSmokingLocation.getLongitude(), mSmokingLocation.getLatitude(), mSmokingLocation.getTitle().toString(), markerId, 0);
                        poiData.endPOIdata();

                        Log.v("test search", mSmokingLocation.getTitle().toString());

                        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
                        mAdapter.add(mSmokingLocation);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
               // searcHRef.addChildEventListener(mChildEventListener);

                mListView.setAdapter(mAdapter);
                */

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        mMapContext.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapContext.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mMapContext.onPause();
    }
    @Override
    public void onStop() {
        mMapContext.onStop();
        super.onStop();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mMapContext.onDestroy();
        super.onDestroy();
    }
}

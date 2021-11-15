package com.viasofts.mygcs;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.model.Marker;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.OverlayImage;
import com.o3dr.android.client.Drone;
import com.o3dr.android.client.apis.ControlApi;
import com.o3dr.android.client.apis.VehicleApi;
import com.o3dr.services.android.lib.coordinate.LatLong;
import com.o3dr.services.android.lib.drone.attribute.AttributeType;
import com.o3dr.services.android.lib.drone.property.GuidedState;
import com.o3dr.services.android.lib.drone.property.VehicleMode;
import com.o3dr.services.android.lib.model.AbstractCommandListener;


//가이드 모드
public class GuideMode extends MainActivity {

    private MainActivity mainActivity;

    private LatLng mGuidedPoint; //가이드모드 목적지 저장
    //static Marker mMarkerGuide = new Marker();    //GCS 위치 표시마커 옵션
    private OverlayImage guideIcon = OverlayImage.fromResource(R.drawable.guide);

    public GuideMode(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }



    public void dialogSimple(Drone drone, PointF point) {
        //34줄 에러 = null 값?? 해결법 모르겠음;;
        mGuidedPoint = marker.getPosition();
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mainActivity);
        alt_bld.setMessage("확인하시면 가이드모드로 전환후 기체가 이동합니다.").
                setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'Yes' Button
                        VehicleApi.getApi(drone).setVehicleMode(VehicleMode.COPTER_GUIDED,
                                new AbstractCommandListener() {
                                    @Override
                                    public void onSuccess() {
                                        ControlApi.getApi(drone).goTo(new LatLong(mGuidedPoint.latitude, mGuidedPoint.longitude), true, null);
                                    }
                                    @Override

                                    public void onError(int i) {

                                    }
                                    @Override
                                    public void onTimeout() {
                                    }
                                });
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("Title");
        // Icon for AlertDialog
        alert.setIcon(R.drawable.drone);
        alert.show();
    }

    public static boolean CheckGoal(final Drone drone, LatLng recentLatLng) {
        GuidedState guidedState = drone.getAttribute(AttributeType.GUIDED_STATE);
        LatLng target = new LatLng(guidedState.getCoordinate().getLatitude(),
                guidedState.getCoordinate().getLongitude());
        return target.distanceTo(recentLatLng) <= 1;
    }
}
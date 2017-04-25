package edu.slu.parks.healthwatch.utils;

import android.content.IntentFilter;

/**
 * Created by okori on 11/3/2016.
 */

public class Constants {
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String CANCEL = "cancel";
    public static final String OK = "ok";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String TAG = "HealthWatch";
    public static final int REQUEST_ACCESS_FINE_LOCATION = 200;
    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 201;
    public static final String MEASURE_DIALOG = "measure_dialog";
    public static final int REQUEST_CHECK_SETTINGS = 100;
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String SYSTOLIC = "systolic";
    public static final String DIASTOLIC = "diastolic";
    public static final String DATE_FORMAT = "MMMM dd, YYYY @ h:mm:ss a";
    public static final String ACTIVE_SECTION = "action_section";
    public static final String HISTORY_DATE_FORMAT = "d MMMM yyyy";
    public static final String NORMAL_DATE_FORMAT = "d MMMM";
    public static final String GRAPH_TYPE = "graph type";
    public static final String DB_DATE_FORMAT = "YYYY-MM-ddTHH:mm:SS.SSS";
    public static final String SELECTED_DATE = "selected_date";
    public static final String SELECTED_VIEW = "selected_view";
    public static final int REQUEST_ENABLE_BT = 300;
    public static final String DEVICE_NAME = "HealthWatch";
    public static final int REQUEST_BT_CANCEL_DISCOVERY = 301;
    public static final String BT_ADDRESS = "mac_address";
    public static final String BT_NAME = "bt_name";
    public static final int MESSAGE_READ = 1;
    public static final String UUID = "7A51FDC2-FDDF-4c9b-AFFC-98BCD91BF93B";
    public static final String HAS_LOGIN = "has_login";
    public static final int REQUEST_FINGERPRINT = 400;
    public static final java.lang.String RSA = "RSA";
    public static final String KEYSTORE = "AndroidKeyStore";
    public static final String ALIAS = "keyHealthWatch";
    public static final java.lang.String SIGNATURE = "SHA256withRSA";
    public static final String PIN = "UserPin";
    public static final String PAUSED = "paused";
    public static final String GOHOME = "next";
    public static final String TEMPORARY_PIN = "temp_pin";
    public static final String TEMPORARY_PIN_ISSUED = "temp_pin_issued";
    public static final String CHECK_LOGIN = "check_login";
    public static final long INTERVAL = 1000;
    public static final String MAC = "macAddress";
    public static final String ARTICLE_URL = "article_url";
    public static final String ARTICLE_TITLE = "article_title";
    public static final String PDF_MIME_TYPE = "application/pdf";

    public static class Gatt {
        public static final String GENERIC_ACCESS_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
        public static final String DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
        public static final String APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
        public static final String PERIPHERAL_PRIVACY_FLAG = "00002a02-0000-1000-8000-00805f9b34fb";
        public static final String RECONNECTION_ADDRESS = "00002a03-0000-1000-8000-00805f9b34fb";
        public static final String PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = "00002a04-0000-1000-8000-00805f9b34fb";

        public static final String GENERIC_ATTRIBUTE_SERVICE = "00001801-0000-1000-8000-00805f9b34fb";
        public static final String SERVICE_CHANGED = "00002a05-0000-1000-8000-00805f9b34fb";

        public static final String BLOOD_PRESSURE_SERVICE = "00001810-0000-1000-8000-00805f9b34fb";
        public static final String BLOOD_PRESSURE_MEASUREMENT = "00002a35-0000-1000-8000-00805f9b34fb";
        public static final String INTERMEDIATE_CUFF_PRESSURE = "00002a36-0000-1000-8000-00805f9b34fb";
        public static final String BLOOD_PRESSURE_FEATURE = "00002a49-0000-1000-8000-00805f9b34fb";

        public static final String GENERIC_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
        public static final String MANUFACTURER_NAME = "00002a29-0000-1000-8000-00805f9b34fb";
        public static final String SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb";
        public static final String MODEL_NUMBER = "00002a24-0000-1000-8000-00805f9b34fb";
        public static final String SERIAL_NUMBER = "00002a25-0000-1000-8000-00805f9b34fb";
        public static final String FIRMWARE_REVISION = "00002a26-0000-1000-8000-00805f9b34fb";
        public static final String HARDWARE_REVISION = "00002a27-0000-1000-8000-00805f9b34fb";
        public static final String SOFTWARE_REVISION = "00002a28-0000-1000-8000-00805f9b34fb";
        public static final String REGULATORY_CERTIFICATION_LIST = "00002a2a-0000-1000-8000-00805f9b34fb";
        public static final String PNP_ID = "00002a50-0000-1000-8000-00805f9b34fb";

        public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
        public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
        public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
        public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
        public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
        public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";


        public static IntentFilter makeGattUpdateIntentFilter() {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.Gatt.ACTION_GATT_CONNECTED);
            intentFilter.addAction(Constants.Gatt.ACTION_GATT_DISCONNECTED);
            intentFilter.addAction(Constants.Gatt.ACTION_GATT_SERVICES_DISCOVERED);
            intentFilter.addAction(Constants.Gatt.ACTION_DATA_AVAILABLE);
            return intentFilter;
        }
    }
}

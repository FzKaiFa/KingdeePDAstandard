package com.fangzuo.assist.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fangzuo.assist.ABase.BaseActivity;
import com.fangzuo.assist.Adapter.BatchNoSpAdapter;
import com.fangzuo.assist.Adapter.EmployeeSpAdapter;
import com.fangzuo.assist.Adapter.PayMethodSpAdapter;
import com.fangzuo.assist.Adapter.PayTypeSpAdapter;
import com.fangzuo.assist.Adapter.PushDownSubListAdapter;
import com.fangzuo.assist.Adapter.StorageSpAdapter;
import com.fangzuo.assist.Adapter.UnitSpAdapter;
import com.fangzuo.assist.Adapter.WaveHouseSpAdapter;
import com.fangzuo.assist.Adapter.YuandanSpAdapter;
import com.fangzuo.assist.Beans.CommonResponse;
import com.fangzuo.assist.Beans.DownloadReturnBean;
import com.fangzuo.assist.Beans.EventBusEvent.ClassEvent;
import com.fangzuo.assist.Beans.GetBatchNoBean;
import com.fangzuo.assist.Beans.PurchaseInStoreUploadBean;
import com.fangzuo.assist.Dao.BarCode;
import com.fangzuo.assist.Dao.Employee;
import com.fangzuo.assist.Dao.InStorageNum;
import com.fangzuo.assist.Dao.PayType;
import com.fangzuo.assist.Dao.Product;
import com.fangzuo.assist.Dao.PurchaseMethod;
import com.fangzuo.assist.Dao.PushDownMain;
import com.fangzuo.assist.Dao.PushDownSub;
import com.fangzuo.assist.Dao.Storage;
import com.fangzuo.assist.Dao.T_Detail;
import com.fangzuo.assist.Dao.T_main;
import com.fangzuo.assist.Dao.Unit;
import com.fangzuo.assist.Dao.WaveHouse;
import com.fangzuo.assist.Dao.YuandanType;
import com.fangzuo.assist.R;
import com.fangzuo.assist.Service.DataService;
import com.fangzuo.assist.Utils.Asynchttp;
import com.fangzuo.assist.Utils.BasicShareUtil;
import com.fangzuo.assist.Utils.CommonMethod;
import com.fangzuo.assist.Utils.Config;
import com.fangzuo.assist.Utils.DataModel;
import com.fangzuo.assist.Utils.DoubleUtil;
import com.fangzuo.assist.Utils.EventBusInfoCode;
import com.fangzuo.assist.Utils.GreenDaoManager;
import com.fangzuo.assist.Utils.Info;
import com.fangzuo.assist.Utils.Lg;
import com.fangzuo.assist.Utils.MathUtil;
import com.fangzuo.assist.Utils.MediaPlayer;
import com.fangzuo.assist.Utils.ShareUtil;
import com.fangzuo.assist.Utils.Toast;
import com.fangzuo.assist.Utils.WebApi;
import com.fangzuo.assist.widget.LoadingUtil;
import com.fangzuo.assist.widget.MyWaveHouseSpinner;
import com.fangzuo.assist.widget.SpinnerPayType;
import com.fangzuo.assist.widget.SpinnerPeople;
import com.fangzuo.assist.widget.SpinnerPurchaseMethod;
import com.fangzuo.assist.widget.SpinnerPurchaseScope;
import com.fangzuo.assist.widget.SpinnerYuanDan;
import com.fangzuo.assist.zxing.CustomCaptureActivity;
import com.fangzuo.greendao.gen.BarCodeDao;
import com.fangzuo.greendao.gen.DaoSession;
import com.fangzuo.greendao.gen.InStorageNumDao;
import com.fangzuo.greendao.gen.ProductDao;
import com.fangzuo.greendao.gen.PushDownMainDao;
import com.fangzuo.greendao.gen.PushDownSubDao;
import com.fangzuo.greendao.gen.T_DetailDao;
import com.fangzuo.greendao.gen.T_mainDao;
import com.fangzuo.greendao.gen.UnitDao;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.loopj.android.http.AsyncHttpClient;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//采购订单下推外购入库
public class PushDownPOActivity extends BaseActivity {
    private int tag = 2;
    private int activity = Config.PushDownPOActivity;

    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.lv_pushsub)
    ListView lvPushsub;
    @BindView(R.id.sp_storage)
    Spinner spStorage;
    @BindView(R.id.sp_wavehouse)
    MyWaveHouseSpinner spWavehouse;
    @BindView(R.id.productName)
    TextView productName;
    @BindView(R.id.sp_unit)
    Spinner spUnit;
    @BindView(R.id.sp_batchNo)
    Spinner spBatchNo;
    @BindView(R.id.ed_num)
    EditText edNum;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.btn_backorder)
    Button btnBackorder;
    @BindView(R.id.btn_checkorder)
    Button btnCheckorder;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_date_pay)
    TextView tvDatePay;
    @BindView(R.id.sp_purchase_scope)
    SpinnerPurchaseScope spPurchaseScope;
    @BindView(R.id.sp_purchaseMethod)
    SpinnerPurchaseMethod spPurchaseMethod;
    @BindView(R.id.sp_yuandan)
    SpinnerYuanDan spYuandan;
    @BindView(R.id.sp_payMethod)
    SpinnerPayType spPayMethod;
    @BindView(R.id.sp_manager)
    SpinnerPeople spManager;
    @BindView(R.id.sp_capture)
    SpinnerPeople spCapture;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.ed_pihao)
    EditText edPihao;
    //    private DaoSession daosession;
    private CommonMethod method;
    private List<PushDownSub> container;
    private ArrayList<String> fidcontainer;
    private List<PushDownSub> list;
    private PushDownSubDao pushDownSubDao;
    private PushDownMainDao pushDownMainDao;
    private String fwanglaiUnit;
    private String employeeId;
    private String departmentId;
    private PushDownSubListAdapter pushDownSubListAdapter;
    private EmployeeSpAdapter employeeAdapter;
    private StorageSpAdapter storageSpinner;
    private PayMethodSpAdapter slaesRange;
    private PayMethodSpAdapter payMethodSpinner;
    private YuandanSpAdapter yuandanSpAdapter;
    private PayTypeSpAdapter payTypeSpAdapter;
    private WaveHouseSpAdapter waveHouseAdapter;
    ArrayList<String> detailContainer;
    //    private String SaleMethodId;
//    private String SaleMethodName;
//    private String saleRangeId;
//    private String saleRangeName;
//    private String yuandanID;
//    private String yuandanName;
//    private String payTypeId;
//    private String payTypeName;
//    private String ManagerId;
//    private String ManagerName;
    private String storageID;
    private String storageName;
    private String waveHouseID;
    private String waveHouseName;
    private PushDownSub pushDownSub;
    private List<Product> products;
    private String productID;
    private String fid;
    private String fentryid;
    private UnitSpAdapter unitAdapter;
    private String unitId;
    private String unitName;
    private double unitrate;
    private double unitrateSub;
    private String fprice;
    private String datePay;
    private boolean isHebing = true;
    private BatchNoSpAdapter batchNoSpAdapter;
    private String batchNo;
    @BindView(R.id.cb_isStorage)
    CheckBox cbIsStorage;
    private boolean isGetDefaultStorage;
    private List<Storage> storages;
    private Product product;
    @BindView(R.id.cb_isAuto)
    CheckBox cbIsAuto;
    private boolean isAuto = false;
    private ShareUtil share;
    //    private String captureID;
//    private String captureName;
    private String billNo;
    private ArrayList<String> fidc;
    private String default_unitID;
    private boolean fBatchManager;
    private boolean fromScan = false;
    private String wavehouseAutoString = "";
    private Storage storage;
    private long ordercode;

    private void ScanBarCode(String barcode) {
        product = null;
        ProductDao productDao = daoSession.getProductDao();
        BarCodeDao barCodeDao = daoSession.getBarCodeDao();
        if (BasicShareUtil.getInstance(mContext).getIsOL()) {
            Asynchttp.post(mContext, getBaseUrl() + WebApi.SEARCHPRODUCTS, barcode, new Asynchttp.Response() {
                @Override
                public void onSucceed(CommonResponse cBean, AsyncHttpClient client) {
                    final DownloadReturnBean dBean = new Gson().fromJson(cBean.returnJson, DownloadReturnBean.class);
                    Log.e("product.size", dBean.products.size() + "");
                    if (dBean.products.size() > 0) {
                        product = dBean.products.get(0);
                        Log.e("product.size", product + "");
                        default_unitID = dBean.products.get(0).FUnitID;
                        fromScan = true;
                        setProduct(product);
                    }
                }

                @Override
                public void onFailed(String Msg, AsyncHttpClient client) {
                    Toast.showText(mContext, Msg);
                }
            });
        } else {
            final List<BarCode> barCodes = barCodeDao.queryBuilder().where(
                    BarCodeDao.Properties.FBarCode.eq(barcode)
            ).build().list();
            if (barCodes.size() > 0) {
                List<Product> products = productDao.queryBuilder().where(
                        ProductDao.Properties.FItemID.eq(barCodes.get(0).FItemID)
                ).build().list();
                if (products != null && products.size() > 0) {
                    product = products.get(0);
                    default_unitID = barCodes.get(0).FUnitID;
                    fromScan = true;
                    setProduct(product);
                }
            } else {
                Toast.showText(mContext, "条码不存在");
                MediaPlayer.getInstance(mContext).error();
            }
        }

    }

    private void setProduct(Product product) {
        if (product != null) {
            boolean flag = true;
            boolean hasUnit = false;
            for (int j = 0; j < pushDownSubListAdapter.getCount(); j++) {
                PushDownSub pushDownSub1 = (PushDownSub) pushDownSubListAdapter.getItem(j);
                if (product.FItemID.equals(pushDownSub1.FItemID)) {
                    if (MathUtil.toD(pushDownSub1.FAuxQty) == MathUtil.toD(pushDownSub1.FQtying)) {
                        flag = true;
                        continue;
                    } else {
//                        for (int i = 0; i < pushDownSubListAdapter.getCount(); i++) {
//                            PushDownSub pushDownSub = (PushDownSub) pushDownSubListAdapter.getItem(i);
                        if (!"".equals(default_unitID)) {
                            if (default_unitID.equals(pushDownSub1.FUnitID)) {
                                flag = false;
                                hasUnit = true;
                                lvPushsub.setSelection(j);
                                lvPushsub.performItemClick(lvPushsub.getChildAt(j), j, lvPushsub.getItemIdAtPosition(j));
                                break;
                            }
                        } else {
                            flag = false;
                            hasUnit = true;
                            lvPushsub.setSelection(j);
                            lvPushsub.performItemClick(lvPushsub.getChildAt(j), j, lvPushsub.getItemIdAtPosition(j));
                            break;
                        }

//                        }
//                        if (!hasUnit){
//                            lvPushsub.setSelection(j);
//                            lvPushsub.performItemClick(lvPushsub.getChildAt(j), j, lvPushsub.getItemIdAtPosition(j));
//                        }

//                        break;
                    }

                }
            }

            if (flag) {
                Toast.showText(mContext, "商品不存在");
                MediaPlayer.getInstance(mContext).error();

            }
        } else {
            Toast.showText(mContext, "列表中不存在商品");
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_push_down_po);
        mContext = this;
        ButterKnife.bind(this);
        share = ShareUtil.getInstance(mContext);
        method = CommonMethod.getMethod(mContext);
        cbIsAuto.setChecked(share.getPDPOisAuto());
        isAuto = share.getPDPOisAuto();
        edPihao.setEnabled(false);
        isGetDefaultStorage = share.getBoolean(Info.Storage + activity);
        cbIsStorage.setChecked(isGetDefaultStorage);
    }

    @Override
    protected void initData() {
        LoadBasicData();
//        LoadBasicData();
        container = new ArrayList<>();
        fidcontainer = getIntent().getExtras().getStringArrayList("fid");
        getList();
        List<PushDownMain> list1 = pushDownMainDao.queryBuilder().where(
                PushDownMainDao.Properties.FInterID.eq(fidcontainer.get(0))
        ).build().list();
        if (list1.size() > 0) {
            fwanglaiUnit = list1.get(0).FSupplyID;
            employeeId = list1.get(0).FEmpID;
            departmentId = list1.get(0).FDeptID;
            billNo = list1.get(0).FBillNo;
        }
        ordercode = DataModel.findOrderCode(mContext, activity, fidcontainer);
        Lg.e("得到ordercode:" + ordercode);

    }

    private void getList() {
        container.clear();
        pushDownSubDao = daoSession.getPushDownSubDao();
        pushDownMainDao = daoSession.getPushDownMainDao();
        for (int i = 0; i < fidcontainer.size(); i++) {
            QueryBuilder<PushDownSub> qb = pushDownSubDao.queryBuilder();
            list = qb.where(PushDownSubDao.Properties.FInterID.eq(fidcontainer.get(i))).build().list();
            container.addAll(list);
        }
        if (container.size() > 0) {
            Lg.e("PushDownSub:" + container.toString());
            pushDownSubListAdapter = new PushDownSubListAdapter(mContext, container);
            lvPushsub.setAdapter(pushDownSubListAdapter);
            pushDownSubListAdapter.notifyDataSetChanged();
        } else {
            Toast.showText(mContext, "未查询到数据");
        }
    }

    private void LoadBasicData() {
        tvDate.setText(getTime(true));
        tvDatePay.setText(getTime(true));
        storageSpinner = method.getStorageSpinner(spStorage);
        spPurchaseMethod.setAutoSelection(getString(R.string.spPurchaseMethod_pd_po), "");
        spPurchaseScope.setAutoSelection(getString(R.string.spPurchaseScope_pd_po), "");
//        spYuandan.setAutoSelection(getString(R.string.spYuandan_pd_po),"");
        spPayMethod.setAutoSelection(getString(R.string.spPayMethod_pd_po), "");
        spManager.setAutoSelection(getString(R.string.spManager_pd_po), "");
        spCapture.setAutoSelection(getString(R.string.spCapture_pd_po), "");

//        slaesRange = method.getPurchaseRange(spPurchaseScope);
//        payMethodSpinner = method.getPayMethodSpinner(spPurchaseMethod);
//        yuandanSpAdapter = method.getyuandanSp(spYuandan);
//        payTypeSpAdapter = method.getpayType(spPayMethod);
//        employeeAdapter = method.getEmployeeAdapter(spManager);
//        spCapture.setAdapter(employeeAdapter);

//        spPurchaseMethod.setSelection(share.getPDPOPurchaseMehtod());
//        spPurchaseScope.setSelection(share.getPDPOPurchaseScop());
//        spYuandan.setSelection(share.getPDPOYuandan());
//        spPayMethod.setSelection(share.getPDMTPayMethod());
//        spManager.setSelection(share.getPDPOManager());
    }

    @Override
    protected void initListener() {
        if (isPhoneScan())ivScan.setVisibility(View.VISIBLE);
        ivScan.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(PushDownPOActivity.this);
                intentIntegrator.setCaptureActivity(CustomCaptureActivity.class);
                intentIntegrator.initiateScan();
            }
        });
        btnBackorder.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (DataModel.checkHasDetail(mContext, activity)) {
//                    btnBackorder.setClickable(false);
//                    LoadingUtil.show(mContext, "正在回单...");
//                    upload();
                    UpLoadActivity.start(mContext,tag,activity);
                } else {
                    Toast.showText(mContext, "无单据信息");
                }
            }
        });
        cbIsAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isAuto = b;
                share.setPDPOisAuto(b);
            }
        });

        cbIsStorage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isGetDefaultStorage = b;
                share.setBooleam(Info.Storage + activity, b);
            }
        });

        //批号输入监听
        edPihao.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Log.e(TAG, "批号监听。。。");
                if (i == 0 && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                    getInstorageNum(product);
                    setfocus(edPihao);
                }
                return true;
            }
        });

//        spCapture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Employee capture = (Employee)employeeAdapter.getItem(i);
//                captureID = capture.FItemID;
//                captureName = capture.FName;
////                share.setPDPOCapture(i);
//                if (isFirst){
//                    share.setPDPOCapture(i);
//                    spCapture.setSelection(i);
//                }
//                else{
//                    spCapture.setSelection(share.getPDPOCapture());
//                    isFirst=true;
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        spPurchaseMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                PurchaseMethod item = (PurchaseMethod) payMethodSpinner.getItem(i);
//                SaleMethodId = item.FItemID;
//                SaleMethodName = item.FName;
////                share.setPDPOPurchaseMehtod(i);
//                if (isFirst2){
//                    share.setPDPOPurchaseMehtod(i);
//                    spPurchaseMethod.setSelection(i);
//                }
//                else{
//                    spPurchaseMethod.setSelection(share.getPDPOPurchaseMehtod());
//                    isFirst2=true;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        spPurchaseScope.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                PurchaseMethod purchaseMethod = (PurchaseMethod) slaesRange.getItem(i);
//                saleRangeId = purchaseMethod.FItemID;
//                saleRangeName = purchaseMethod.FName;
////                share.setPDPOPurchaseScop(i);
//                if (isFirst3){
//                    share.setPDPOPurchaseScop(i);
//                    spPurchaseScope.setSelection(i);
//                }
//                else{
//                    spPurchaseScope.setSelection(share.getPDPOPurchaseScop());
//                    isFirst3=true;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        spYuandan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                YuandanType yuandanType = (YuandanType) yuandanSpAdapter.getItem(i);
//                yuandanID = yuandanType.FID;
//                yuandanName = yuandanType.FName_CHS;
////                share.setPDPOYuandan(i);
//                if (isFirst4){
//                    share.setPDPOYuandan(i);
//                    spYuandan.setSelection(i);
//                }
//                else{
//                    spYuandan.setSelection(share.getPDPOYuandan());
//                    isFirst4=true;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        spPayMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                share.setSOPayMethod(i);
//                PayType payType = (PayType) payTypeSpAdapter.getItem(i);
//                payTypeId = payType.FItemID;
//                payTypeName = payType.FName;
////                share.setPDPOPayMethod(i);
//                if (isFirst5){
//                    share.setPDPOPayMethod(i);
//                    spPayMethod.setSelection(i);
//                }
//                else{
//                    spPayMethod.setSelection(share.getPDPOPayMethod());
//                    isFirst5=true;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


//        spManager.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Employee employee = (Employee) employeeAdapter.getItem(i);
//                ManagerId = employee.FItemID;
//                ManagerName = employee.FName;
////                share.setPDPOManager(i);
//                if (isFirst6){
//                    share.setPDPOManager(i);
//                    spManager.setSelection(i);
//                }
//                else{
//                    spManager.setSelection(share.getPDPOManager());
//                    isFirst6=true;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        spUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Unit unit = (Unit) unitAdapter.getItem(i);
                if (unit != null) {
                    unitId = unit.FMeasureUnitID;
                    unitName = unit.FName;
                    unitrate = MathUtil.toD(unit.FCoefficient);
                    Log.e("1111", unitrate + "");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spStorage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                storage = (Storage) storageSpinner.getItem(i);
                waveHouseID = "0";
                storageID = storage.FItemID;
                storageName = storage.FName;
//                waveHouseAdapter = CommonMethod.getMethod(mContext).getWaveHouseAdapter(storage, spWavehouse);
                spWavehouse.setAuto(mContext, storage, wavehouseAutoString);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spWavehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                WaveHouse waveHouse = (WaveHouse) spWavehouse.getAdapter().getItem(i);
                waveHouseID = waveHouse.FSPID;
                waveHouseName = waveHouse.FName;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        lvPushsub.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pushDownSub = (PushDownSub) pushDownSubListAdapter.getItem(i);
                getUnitrateSub(pushDownSub);
                ProductDao productDao = daoSession.getProductDao();
                if (BasicShareUtil.getInstance(mContext).getIsOL()) {
                    Asynchttp.post(mContext, getBaseUrl() + WebApi.PRPDUCTSEARCHWHERE, pushDownSub.FItemID, new Asynchttp.Response() {
                        @Override
                        public void onSucceed(CommonResponse cBean, AsyncHttpClient client) {
                            final DownloadReturnBean dBean = new Gson().fromJson(cBean.returnJson, DownloadReturnBean.class);
                            Log.e("product.size", dBean.products.size() + "");
                            if (dBean.products.size() > 0) {
                                product = dBean.products.get(0);
                                Log.e("product.size", product + "");
                                clickList(product);
                            }
                        }

                        @Override
                        public void onFailed(String Msg, AsyncHttpClient client) {
                            Toast.showText(mContext, Msg);
                        }
                    });
                } else {
                    products = productDao.queryBuilder().where(
                            ProductDao.Properties.FItemID.eq(pushDownSub.FItemID)
                    ).build().list();
                    if (products.size() > 0) {
                        product = products.get(0);
                        clickList(product);
                    }
                }

            }
        });
        //防止ScrollView与ListView滑动冲突
        lvPushsub.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

//        spBatchNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                InStorageNum inStorageNum = (InStorageNum) batchNoSpAdapter.getItem(i);
//                batchNo = inStorageNum.FBatchNo;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
    }

    //获取明细里面的单位的换算率
    private void getUnitrateSub(PushDownSub pushDownSub) {
        UnitDao unitDao = daoSession.getUnitDao();
        List<Unit> units = unitDao.queryBuilder().where(
                UnitDao.Properties.FMeasureUnitID.eq(pushDownSub.FUnitID)
        ).build().list();
        if (units.size() > 0) {
            unitrateSub = MathUtil.toD(units.get(0).FCoefficient);
            Lg.e("获得明细换算率：" + unitrateSub);
        } else {
            unitrateSub = 1;
            Lg.e("获得明细换算率失败：" + unitrateSub);
        }
    }

    private void clickList(final Product product) {
        productName.setText(product.FName);
        productID = pushDownSub.FItemID;
        wavehouseAutoString = product.FSPID;
        if ((product.FBatchManager) != null && (product.FBatchManager).equals("1")) {
            fBatchManager = true;
            spBatchNo.setEnabled(true);
            edPihao.setEnabled(true);
        } else {
            edPihao.setText("");
            edPihao.setEnabled(false);
            spBatchNo.setEnabled(false);
            fBatchManager = false;
        }
        if (isGetDefaultStorage) {
            for (int j = 0; j < storageSpinner.getCount(); j++) {
                if (((Storage) storageSpinner.getItem(j)).FItemID.equals(product.FDefaultLoc)) {
                    spStorage.setSelection(j);
                    break;
                }
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    spWavehouse.setAuto(mContext, storage, wavehouseAutoString);
//                    for (int j = 0; j < waveHouseAdapter.getCount(); j++) {
//                        if (((WaveHouse)waveHouseAdapter.getItem(j)).FSPID.equals(product.FSPID)) {
//                            spWavehouse.setSelection(j);
//                            break;
//                        }
//                    }
                }
            }, 50);
        }
        fid = pushDownSub.FInterID;
        fentryid = pushDownSub.FEntryID;
        fprice = pushDownSub.FAuxPrice;
        unitAdapter = CommonMethod.getMethod(mContext).getUnitAdapter(product.FUnitGroupID, spUnit);

        if (fromScan) {
            chooseUnit(default_unitID);
        } else {
            chooseUnit(pushDownSub.FUnitID);
        }
        fromScan = false;
//        getBatchNo();
        if (isAuto) {
            edNum.setText("1.0");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Addorder();
                }
            }, 100);
        }
    }

    //定位单位
    private void chooseUnit(String str) {
        if (str != null) {
            for (int i = 0; i < unitAdapter.getCount(); i++) {
                if (((Unit) unitAdapter.getItem(i)).FMeasureUnitID.equals(str)) {
                    Lg.e("定位单位：" + unitAdapter.getItem(i).toString());
                    spUnit.setSelection(i);
                }
            }
        }

    }

    @Override
    protected void OnReceive(String code) {
        ScanBarCode(code);

    }


    @OnClick({R.id.btn_add, R.id.btn_checkorder, R.id.tv_date, R.id.tv_date_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                Addorder();
                break;
            case R.id.btn_checkorder:
                Bundle b = new Bundle();
                b.putInt("activity", activity);
                startNewActivity(Table3Activity.class, 0, 0, false, b);
                break;
            case R.id.tv_date:
                datePicker(tvDate);
                break;
            case R.id.tv_date_pay:
                datePicker(tvDatePay);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("resume", "resume");
        getList();
    }


    private void Addorder() {
        try {
            if (product != null) {
                batchNo = edPihao.getText().toString();
                String discount = "";
                String num = edNum.getText().toString();
                if (fBatchManager && "".equals(batchNo)) {
                    Toast.showText(mContext, "请输入批次");
                    return;
                }
                if (edNum.getText().toString().equals("") || edNum.getText().toString().equals("0")) {
                    MediaPlayer.getInstance(mContext).error();
                    Toast.showText(mContext, "请输入数量");
                    return;
                }
                if (fid == null) {
                    MediaPlayer.getInstance(mContext).error();
                    Toast.showText(mContext, "请选择单据");
                    return;
                }
                if (MathUtil.toD(pushDownSub.FAuxQty) < ((MathUtil.toD(num) * unitrate) / unitrateSub + MathUtil.toD(pushDownSub.FQtying))) {
                    MediaPlayer.getInstance(mContext).error();
                    Toast.showText(mContext, "大兄弟,您的数量超过我的想象");
                    return;
                }

                LoadingUtil.show(mContext,"请稍后...");
//            if (edNum.getText().toString().equals("0")
//                    ||edNum.getText().toString().equals("")
//                    || fid == null
//                    || MathUtil.toD(pushDownSub.FAuxQty) < ((MathUtil.toD(num) * unitrate) + MathUtil.toD(pushDownSub.FQtying))) {
//                pg.dismiss();
//                if (edNum.getText().toString().equals("")||edNum.getText().toString().equals("0")) {
//                    MediaPlayer.getInstance(mContext).error();
//                    Toast.showText(mContext, "请输入数量");
//                } else if (fid == null) {
//                    MediaPlayer.getInstance(mContext).error();
//                    Toast.showText(mContext, "请选择单据");
//                } else if (MathUtil.toD(pushDownSub.FAuxQty) < ((MathUtil.toD(num)) + MathUtil.toD(pushDownSub.FQtying))) {
//                    MediaPlayer.getInstance(mContext).error();
//                    Toast.showText(mContext, "大兄弟,您的数量超过我的想象");
//                }
//            } else {
                if (isHebing) {
                    List<T_Detail> detailhebing = t_detailDao.queryBuilder().where(
                            T_DetailDao.Properties.Activity.eq(activity),
                            T_DetailDao.Properties.FInterID.eq(fid),
                            T_DetailDao.Properties.FProductId.eq(product.FItemID == null ? "" : product.FItemID),
                            T_DetailDao.Properties.FStorageId.eq(storageID),
                            T_DetailDao.Properties.FPositionId.eq(spWavehouse.getWaveHouseId()),
                            T_DetailDao.Properties.FUnitId.eq(unitId),
                            T_DetailDao.Properties.FEntryID.eq(fentryid),
                            T_DetailDao.Properties.FBatch.eq(batchNo == null ? "0" : batchNo)
                    ).build().list();
                    if (detailhebing.size() > 0) {
                        for (int i = 0; i < detailhebing.size(); i++) {
                            num = (MathUtil.toD(num) + MathUtil.toD(detailhebing.get(i).FQuantity)) + "";
                            List<T_main> t_mainList = t_mainDao.queryBuilder().where(
                                    T_mainDao.Properties.FIndex.eq(detailhebing.get(i).FIndex)).build().list();
                            if (t_mainList.size() > 0) {
                                t_mainDao.delete(t_mainList.get(0));
                            }
                            t_detailDao.delete(detailhebing.get(i));
                        }
                    }
                }
                t_mainDao.deleteInTx(t_mainDao.queryBuilder().where(T_mainDao.Properties.OrderId.eq(ordercode)).build().list());
                String second = getTimesecond();
                T_main t_main = new T_main();
                t_main.FDepartment = "";
                t_main.FDepartmentId = departmentId == null ? "" : departmentId;
                t_main.FIndex = second;
                t_main.FPaymentDate = tvDatePay.getText().toString();
                t_main.orderId = ordercode;
                t_main.orderDate = tvDate.getText().toString();
                t_main.FPurchaseUnit = "";
                t_main.FSalesMan = "";
                t_main.FSalesManId = employeeId == null ? "" : employeeId;
                t_main.FMaker = share.getUserName();
                t_main.FMakerId = share.getsetUserID();
                t_main.FDirector = spManager.getEmployeeName();
                t_main.FDirectorId = spManager.getEmployeeId();
                t_main.FPaymentType = spPayMethod.getDataName();
                t_main.FPaymentTypeId = spPayMethod.getDataId();
                t_main.saleWayId = spPurchaseMethod.getDataId();
                t_main.saleWay = spPurchaseScope.getDataName();
                t_main.FDeliveryAddress = "";
                t_main.FRemark = "";
                t_main.FCustody = "";
                t_main.FCustodyId = spPurchaseScope.getDataId();
                t_main.FAcount = "";
                t_main.FAcountID = "";
                t_main.Rem = "";
                t_main.supplier = spCapture.getEmployeeId();
                t_main.supplierId = fwanglaiUnit == null ? "" : fwanglaiUnit;
                t_main.FSendOutId = "";
                t_main.FDeliveryType = fid == null ? "" : fid;
                t_main.activity = activity;
                t_main.sourceOrderTypeId = spYuandan.getDataId();
                long insert1 = t_mainDao.insert(t_main);

                T_Detail t_detail = new T_Detail();
                t_detail.FBillNo = billNo;
                t_detail.FBatch = batchNo == null ? "" : batchNo;
                t_detail.FOrderId = ordercode;
                t_detail.FProductId = product.FItemID;
                t_detail.FProductName = product.FName;
                t_detail.FProductCode = product.FNumber;
                t_detail.FIndex = second;
                t_detail.FUnitId = unitId == null ? "" : unitId;
                t_detail.FUnit = unitName == null ? "" : unitName;
                t_detail.FStorage = storageName == null ? "" : storageName;
                t_detail.FStorageId = storageID == null ? "" : storageID;
                t_detail.FPosition = spWavehouse.getWaveHouse();
                t_detail.FPositionId = spWavehouse.getWaveHouseId();
                t_detail.activity = activity;
                t_detail.FDiscount = discount;
                t_detail.FQuantity = num;
                t_detail.unitrate = unitrate;
                t_detail.FTaxUnitPrice = fprice == null ? "" : fprice;
                t_detail.FEntryID = fentryid == null ? "" : fentryid;
                t_detail.FInterID = fid == null ? "" : fid;
                long insert = t_detailDao.insert(t_detail);

                if (insert1 > 0 && insert > 0) {
                    pushDownSub.FQtying = DoubleUtil.sum(MathUtil.toD(pushDownSub.FQtying), (MathUtil.toD(edNum.getText().toString()) * unitrate) / unitrateSub) + "";
                    pushDownSubDao.update(pushDownSub);
                    Toast.showText(mContext, "添加成功");
                    MediaPlayer.getInstance(mContext).ok();
                    pushDownSubListAdapter.notifyDataSetChanged();
                    resetAll();
                    LoadingUtil.dismiss();
                } else {
                    Toast.showText(mContext, "添加失败，请重试");
                    MediaPlayer.getInstance(mContext).error();
                    LoadingUtil.dismiss();
                }

            } else {
                Toast.showText(mContext, "未选中物料");
            }
        } catch (Exception e) {
            DataService.pushError(mContext, this.getClass().getSimpleName(), e);
        }
    }

    private void resetAll() {
        edNum.setText("");
        productName.setText("");
        product = null;
        edPihao.setText("");
    }

    private void getBatchNo() {
//        if (waveHouseID == null) {
//            waveHouseID = "0";
//        }
        if (fBatchManager) {
            if (BasicShareUtil.getInstance(mContext).getIsOL()) {
                final List<InStorageNum> container = new ArrayList<>();
                GetBatchNoBean gBean = new GetBatchNoBean();
                gBean.ProductID = productID;
                gBean.StorageID = storageID;
                gBean.WaveHouseID = spWavehouse.getWaveHouseId();
                String json = new Gson().toJson(gBean);
                Asynchttp.post(mContext, getBaseUrl() + WebApi.GETPICI, json, new Asynchttp.Response() {
                    @Override
                    public void onSucceed(CommonResponse cBean, AsyncHttpClient client) {
                        Log.e(TAG, "getPici获取数据：" + cBean.returnJson);
                        DownloadReturnBean downloadReturnBean = new Gson().fromJson(cBean.returnJson, DownloadReturnBean.class);
                        if (downloadReturnBean.InstorageNum != null) {
                            container.addAll(downloadReturnBean.InstorageNum);
                            batchNoSpAdapter = new BatchNoSpAdapter(mContext, container);
                            spBatchNo.setAdapter(batchNoSpAdapter);
                            batchNoSpAdapter.notifyDataSetChanged();
                        } else {

                        }
//                    DownloadReturnBean dBean = new Gson().fromJson(cBean.returnJson, DownloadReturnBean.class);
//                    if(dBean.InstorageNum!=null){
//                        for (int i = 0; i < dBean.InstorageNum.size(); i++) {
//                            if (dBean.InstorageNum.get(i).FQty != null
//                                    && MathUtil.toD(dBean.InstorageNum.get(i).FQty) > 0) {
//                                Log.e(TAG,"有库存的批次："+dBean.InstorageNum.get(i).toString());
//                                container.add(dBean.InstorageNum.get(i));
//                            }
//                        }
//                        pici = new PiciSpAdapter(mContext, container);
//                        spPihao.setAdapter(pici);
//                    }
//                    pici.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(String Msg, AsyncHttpClient client) {
                        Log.e(TAG, "getPici获取数据错误：" + Msg);
                        Toast.showText(mContext, Msg);
                    }
                });
            } else {
                InStorageNumDao inStorageNumDao = daoSession.getInStorageNumDao();
                List<InStorageNum> inStorageNa = inStorageNumDao.queryBuilder().where(
                        InStorageNumDao.Properties.FStockID.eq(storageID),
                        InStorageNumDao.Properties.FStockPlaceID.eq(spWavehouse.getWaveHouseId()),
                        InStorageNumDao.Properties.FItemID.eq(productID)
                ).build().list();
                if (inStorageNa.size() > 0) {
                    batchNoSpAdapter = new BatchNoSpAdapter(mContext, inStorageNa);
                    spBatchNo.setAdapter(batchNoSpAdapter);
                    batchNoSpAdapter.notifyDataSetChanged();
                }
            }
        }


    }

    private void upload() {
        PurchaseInStoreUploadBean pBean = new PurchaseInStoreUploadBean();
        fidc = new ArrayList<>();
        ArrayList<PurchaseInStoreUploadBean.purchaseInStore> data = new ArrayList<>();
        List<T_main> mainsTemp = t_mainDao.queryBuilder().where(T_mainDao.Properties.Activity.eq(activity)).build().list();
        Lg.e(mainsTemp.size() + "");
        TreeSet<Long> getFids = new TreeSet<>();
        for (int i = 0; i < mainsTemp.size(); i++) {
            getFids.add(mainsTemp.get(i).orderId);
        }
        for (Long str : getFids) {
            List<T_main> mains = t_mainDao.queryBuilder().where(
                    T_mainDao.Properties.Activity.eq(activity),
                    T_mainDao.Properties.OrderId.eq(str)
            ).build().list();
            for (int i = 0; i < mains.size(); i++) {
//                if (i > 0 && mains.get(i).FDeliveryType.equals(mains.get(i - 1).FDeliveryType)) {
                if (i > 0 && mains.get(i).orderId == (mains.get(i - 1).orderId)) {
                    fidc.add(mains.get(i).FDeliveryType);
                } else {
                    PurchaseInStoreUploadBean.purchaseInStore puBean = pBean.new purchaseInStore();
                    detailContainer = new ArrayList<>();
                    String main;
                    String detail = "";
                    T_main t_main = mains.get(i);
                    fidc.add(t_main.FDeliveryType);
                    main = t_main.FMakerId + "|" +
                            t_main.orderDate + "|" +
                            t_main.FPaymentDate + "|" +
                            t_main.supplierId + "|" +
                            t_main.saleWayId + "|" +
                            "1" + "|" +
                            t_main.FDepartmentId + "|" +
                            t_main.FSalesManId + "|" +
                            t_main.FDirectorId + "|" +
                            "0" + "|" +
                            t_main.supplier + "|";
                    puBean.main = main;
                    List<T_Detail> details = t_detailDao.queryBuilder().where(
                            T_DetailDao.Properties.FOrderId.eq(t_main.orderId),
                            T_DetailDao.Properties.Activity.eq(activity)
                    ).build().list();
                    for (int j = 0; j < details.size(); j++) {
                        if (j != 0 && j % 49 == 0) {
                            Log.e("j%49", j % 49 + "");
                            T_Detail t_detail = details.get(j);
                            detail = detail +
                                    t_detail.FProductId + "|" +
                                    t_detail.FUnitId + "|" +
                                    t_detail.FTaxUnitPrice + "|" +
                                    t_detail.FQuantity + "|" +
                                    t_detail.FStorageId + "|" +
                                    t_detail.FPositionId + "|" +
                                    t_detail.FEntryID + "|" +
                                    t_detail.FInterID + "|" +
                                    t_detail.FBatch + "|";
                            detail = detail.subSequence(0, detail.length() - 1).toString();
                            detailContainer.add(detail);
                            detail = "";
                        } else {
                            Log.e("j", j + "");
                            Log.e("details.size()", details.size() + "");
                            T_Detail t_detail = details.get(j);
                            detail = detail +
                                    t_detail.FProductId + "|" +
                                    t_detail.FUnitId + "|" +
                                    t_detail.FTaxUnitPrice + "|" +
                                    t_detail.FQuantity + "|" +
                                    t_detail.FStorageId + "|" +
                                    t_detail.FPositionId + "|" +
                                    t_detail.FEntryID + "|" +
                                    t_detail.FInterID + "|" +
                                    t_detail.FBatch + "|";
                            Log.e("detail1", detail);
                        }

                    }
                    if (detail.length() > 0) {
                        detail = detail.subSequence(0, detail.length() - 1).toString();
                    }

                    Log.e("detail", detail);
                    detailContainer.add(detail);
                    puBean.detail = detailContainer;
                    data.add(puBean);
                }

            }
        }
        pBean.list = data;
        DataModel.upload( WebApi.PUSHDOWNPOUPLOAD, gson.toJson(pBean));
//        postToServer(data);

    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void receiveEvent(ClassEvent event) {
        switch (event.Msg) {
            case EventBusInfoCode.ScanResult://
                BarcodeResult res = (BarcodeResult) event.postEvent;
                OnReceive(res.getResult().getText());
                break;
//            case EventBusInfoCode.Upload_OK://回单成功
//                t_detailDao.deleteInTx(t_detailDao.queryBuilder().where(
//                        T_DetailDao.Properties.Activity.eq(activity)
//                ).build().list());
//                t_mainDao.deleteInTx(t_mainDao.queryBuilder().where(
//                        T_mainDao.Properties.Activity.eq(activity)
//                ).build().list());
//                for (int i = 0; i < fidc.size(); i++) {
//                    pushDownSubDao.deleteInTx(pushDownSubDao.queryBuilder().where(
//                            PushDownSubDao.Properties.FInterID.eq(fidc.get(i))).build().list());
//                    pushDownMainDao.deleteInTx(pushDownMainDao.queryBuilder().where(
//                            PushDownMainDao.Properties.FInterID.eq(fidc.get(i))).build().list());
//                }
//                btnBackorder.setClickable(true);
//                LoadingUtil.dismiss();
//                Toast.showText(mContext, "上传成功");
//                MediaPlayer.getInstance(mContext).ok();
//                Bundle b = new Bundle();
//                b.putInt("123", tag);
//                startNewActivity(PushDownPagerActivity.class, 0, 0, true, b);
//                break;
//            case EventBusInfoCode.Upload_Error://回单失败
//                String error = (String) event.postEvent;
//                Toast.showText(mContext, error);
//                btnBackorder.setClickable(true);
//                LoadingUtil.dismiss();
//                MediaPlayer.getInstance(mContext).error();
//                break;
            case EventBusInfoCode.Close_Activity://关闭
                onBackPressed();
                break;
        }
    }

    private void postToServer(ArrayList<PurchaseInStoreUploadBean.purchaseInStore> data) {
        PurchaseInStoreUploadBean pBean = new PurchaseInStoreUploadBean();
        pBean.list = data;
        Gson gson = new Gson();
        Asynchttp.post(mContext, getBaseUrl() + WebApi.PUSHDOWNPOUPLOAD, gson.toJson(pBean), new Asynchttp.Response() {
            @Override
            public void onSucceed(CommonResponse cBean, AsyncHttpClient client) {
                MediaPlayer.getInstance(mContext).ok();
                Toast.showText(mContext, "上传成功");
                List<T_Detail> list = t_detailDao.queryBuilder().where(
                        T_DetailDao.Properties.Activity.eq(activity)
                ).build().list();
                List<T_main> list1 = t_mainDao.queryBuilder().where(
                        T_mainDao.Properties.Activity.eq(activity)
                ).build().list();
                for (int i = 0; i < list.size(); i++) {
                    t_detailDao.delete(list.get(i));
                }
                for (int i = 0; i < list1.size(); i++) {
                    t_mainDao.delete(list1.get(i));
                }
                for (int i = 0; i < fidc.size(); i++) {
                    List<PushDownSub> pushDownSubs = pushDownSubDao.queryBuilder().where(
                            PushDownSubDao.Properties.FInterID.eq(fidc.get(i))
                    ).build().list();
                    pushDownSubDao.deleteInTx(pushDownSubs);
                    List<PushDownMain> pushDownMains = pushDownMainDao.queryBuilder().where(
                            PushDownMainDao.Properties.FInterID.eq(fidc.get(i))
                    ).build().list();
                    pushDownMainDao.deleteInTx(pushDownMains);
                }
                btnBackorder.setClickable(true);
                LoadingUtil.dismiss();
                Bundle b = new Bundle();
                b.putInt("123", tag);
                startNewActivity(PushDownPagerActivity.class, 0, 0, true, b);
            }

            @Override
            public void onFailed(String Msg, AsyncHttpClient client) {
                MediaPlayer.getInstance(mContext).error();
                btnBackorder.setClickable(true);
                Toast.showText(mContext, Msg);
                LoadingUtil.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bundle b = new Bundle();
        b.putInt("123", tag);
        startNewActivity(PushDownPagerActivity.class, 0, 0, true, b);
    }
}

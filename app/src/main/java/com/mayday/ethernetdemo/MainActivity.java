package com.mayday.ethernetdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mayday.ethernetdemo.util.IpUtil;
import com.mayday.ethernetdemo.util.NetWorkType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button open, close, dhcp_button, static_button;
    private EthernetMain ethernetMain;
    private EditText etIp;
    private EditText etZwym;
    private EditText etIpWg;
    private EditText etDns1;
    private EditText etDns2;
    private TextView tvIpContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        open = (Button) findViewById(R.id.open);
        close = (Button) findViewById(R.id.close);
        dhcp_button = (Button) findViewById(R.id.dhcp_button);
        static_button = (Button) findViewById(R.id.static_button);

        ethernetMain = new EthernetMain(this);

        open.setOnClickListener(this);
        close.setOnClickListener(this);
        dhcp_button.setOnClickListener(this);
        static_button.setOnClickListener(this);
        initView();
        initData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open:
                ethernetMain.openEth();
                break;

            case R.id.close:
                ethernetMain.closeEth();
                break;

            case R.id.dhcp_button:
                ethernetMain.dhcpEth();
                break;

            case R.id.static_button:
                /**
                 * @param ip IP地址
                 * @param fix 子网掩码
                 * @param dns1 DNS1
                 * @param dns2 DNS2
                 * @param gw 默认网关
                 */
                setStaticIP();
                break;
        }
    }

    private void setStaticIP() {
//                ethernetMain.staticEth("192.168.1.222","255.255.255.0","192.168.1.1","192.168.1.1","192.168.1.1");
        String ip = etIp.getText().toString().trim();
        String zwym = etZwym.getText().toString().trim();
        String ipwg = etIpWg.getText().toString().trim();
        String dns1 = etDns1.getText().toString().trim();
        String dns2 = etDns2.getText().toString().trim();
        if(       NetUtil.isValidIpAddress(ip)
                &&NetUtil.isValidIpAddress(zwym)
                &&NetUtil.isValidIpAddress(ipwg)
                &&NetUtil.isValidIpAddress(dns1)
                &&NetUtil.isValidIpAddress(dns2)){

            ethernetMain.staticEth(ip, zwym, dns1, dns2, ipwg);
        }else {
            Toast.makeText(this,"ip错误！",Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        etIp = (EditText) findViewById(R.id.et_ip);
        etZwym = (EditText) findViewById(R.id.et_zwym);
        etIpWg = (EditText) findViewById(R.id.et_ip_wg);
        etDns1 = (EditText) findViewById(R.id.et_dns1);
        etDns2 = (EditText) findViewById(R.id.et_dns2);
        tvIpContent = (TextView) findViewById(R.id.tv_ip_content);
    }

    private void initData() {
        Observable.just("one")
                .map(new Function<String, NetWorkType>() {
                    @Override
                    public NetWorkType apply(String s) throws Exception {
                        NetWorkType netWorkType = IpUtil.getNetWorkType();
                        return netWorkType;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NetWorkType>() {
                    @Override
                    public void accept(NetWorkType s) throws Exception {
                        tvIpContent.setText("ip:" + s.getIp() + "\n" +
                                "type:" + s.getType());
                    }
                });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        if (NetChangeReceiver.NET_CHANGE.equals(event)) {
            initData();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}

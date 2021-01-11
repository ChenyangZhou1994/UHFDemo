package com.example.uhf.fragment;

import com.example.uhf.R;
import com.example.uhf.UHFMainActivity;
import com.example.uhf.UIHelper;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.rscja.utility.StringUtility;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

public class UHFSetFragment extends KeyDwonFragment   {
	private UHFMainActivity mContext;

	private Button btnSetFre;
	private Button btnGetFre;
	private Spinner spMode;

	@ViewInject(R.id.btnSetPower)
	private Button btnSetPower;
	@ViewInject(R.id.btnGetPower)
	private Button btnGetPower;
	@ViewInject(R.id.spPower)
	private Spinner spPower;
	@ViewInject(R.id.et_worktime)
	private EditText et_worktime;
	@ViewInject(R.id.et_waittime)
	private EditText et_waittime;
	@ViewInject(R.id.btnWorkWait)
	private Button btnWorkWait;

	@ViewInject(R.id.cbTagFocus)
    private CheckBox cbTagFocus; //打开tagFocus
    @ViewInject(R.id.cbFastID)
    private CheckBox cbFastID; //打开FastID
    @ViewInject(R.id.cbEPC_TID)
    private CheckBox cbEPC_TID; //打开EPC+TID
    
	 @ViewInject(R.id.spsession)//session
	    private  Spinner SpSession;

	    @ViewInject(R.id.spinv)//
	    private  Spinner SpInv;

	    @ViewInject(R.id.btnGetSession)
	    private  Button btnGetSession;

	    @ViewInject(R.id.btnSetSession)
	    private  Button btnSetSession;
	    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater
				.inflate(R.layout.activity_uhfset, container, false);
		ViewUtils.inject(this, root);

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mContext = (UHFMainActivity) getActivity();

		btnSetFre = (Button) mContext.findViewById(R.id.BtSetFre);
		btnGetFre = (Button) mContext.findViewById(R.id.BtGetFre);

		spMode = (Spinner) mContext.findViewById(R.id.SpinnerMode);

		btnSetFre.setOnClickListener(new SetFreOnclickListener());
		btnGetFre.setOnClickListener(new GetFreOnclickListener());
		btnWorkWait.setOnClickListener(new SetPWMOnclickListener());

		 cbTagFocus.setOnCheckedChangeListener(new OnMyCheckedChangedListener());
	        cbFastID.setOnCheckedChangeListener(new OnMyCheckedChangedListener());
	        cbEPC_TID.setOnCheckedChangeListener(new OnMyCheckedChangedListener());
	        
		 SpSession=(Spinner)getView().findViewById(R.id.spsession);
	        SpInv=(Spinner)getView().findViewById(R.id.spinv);
	        btnSetSession.setOnClickListener(new SetSessionOnClickListener());
	        btnGetSession.setOnClickListener(new GetSessionOnClickListener());
	        SpSession.setSelection(1);
	        SpInv.setSelection(0);
	}

	@Override
	public void onResume() {
		super.onResume();
		getFre();
		getPwm();
		OnClick_GetPower(null);

	}

	
	public class OnMyCheckedChangedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.cbTagFocus:
                    if (mContext.mReader.setTagFocus(isChecked)) {
                        if (isChecked) {
                            cbTagFocus.setText(R.string.tagFocus_off);
                        } else {
                            cbTagFocus.setText(R.string.tagFocus);
                        }
                        UIHelper.ToastMessage(mContext,
                                R.string.uhf_msg_set_succ);
                    } else {
                        UIHelper.ToastMessage(mContext,
                                R.string.uhf_msg_set_fail);
//                        mContext.playSound(2);
                    }
                    break;
                case R.id.cbFastID:
                    if (mContext.mReader.setFastID(isChecked)) {
                        if (isChecked) {
                            cbFastID.setText(R.string.fastID_off);
                        } else {
                            cbFastID.setText(R.string.fastID);
                        }
                        UIHelper.ToastMessage(mContext,
                                R.string.uhf_msg_set_succ);
                    } else {
                        UIHelper.ToastMessage(mContext,
                                R.string.uhf_msg_set_fail);
//                        mContext.playSound(2);
                    }
                    break;
                case R.id.cbEPC_TID:
                    if (mContext.mReader.setEPCTIDMode(isChecked)) {
                        if (isChecked) {
                            cbEPC_TID.setText(R.string.EPC_TID_off);
                        } else {
                            cbEPC_TID.setText(R.string.EPC_TID);
                        }
                        UIHelper.ToastMessage(mContext,
                                R.string.uhf_msg_set_succ);
                    } else {
                        UIHelper.ToastMessage(mContext,
                                R.string.uhf_msg_set_fail);
//                        mContext.playSound(2);
                    }
                    break;
            }
        }
    }
	
	public class SetSessionOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			 // //设置SESSION只针对盘点EPC有效，对返回EPC+TID,EPC+TID+USER无效
            int seesionid =SpSession .getSelectedItemPosition();
            int inventoried = SpInv.getSelectedItemPosition();
            if(seesionid<0 || inventoried<0){
                return;
            }
            char[] p=mContext.mReader.GetGen2();
            if(p!=null && p.length>=14) {
                int target = p[0];
                int action = p[1];
                int t = p[2];
                int q = p[3];
                int startQ = p[4];
                int minQ = p[5];
                int maxQ = p[6];
                int dr = p[7];
                int coding = p[8];
                int p1 = p[9];
                int Sel = p[10];
                int Session = p[11];
                int g = p[12];
                int linkFrequency = p[13];
                StringBuilder sb=new StringBuilder();
                sb.append("target=");sb.append(target);
                sb.append(" ,action=");sb.append(action);
                sb.append(" ,t=");sb.append(t);
                sb.append(" ,q=");sb.append(q);
                sb.append(" startQ=");sb.append(startQ);
                sb.append(" minQ=");sb.append(minQ);
                sb.append(" maxQ=");sb.append(maxQ);
                sb.append(" dr=");sb.append(dr);
                sb.append(" coding=");sb.append(coding);
                sb.append(" p=");sb.append(p1);
                sb.append(" Sel=");sb.append(Sel);
                sb.append(" Session=");sb.append(Session);
                sb.append(" g=");sb.append(g);
                sb.append(" linkFrequency=");sb.append(linkFrequency);
                sb.append("seesionid=");sb.append(seesionid);
                sb.append(" inventoried=");sb.append(inventoried);
                Log.i("Session",sb.toString());
                if(mContext.mReader.SetGen2(target, action, t, q, startQ, minQ, maxQ, dr, coding, p1, Sel, seesionid, inventoried, linkFrequency)){
                    UIHelper.ToastMessage(mContext,
                            R.string.uhf_msg_Session_Success);
                }else{
                    UIHelper.ToastMessage(mContext,
                            R.string.uhf_msg_Session_Fail);
                }
            }else{
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_Session_Fail);
            }
		}
		
		}
	
	public class GetSessionOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			char[] pp=mContext.mReader.GetGen2();
            if(pp!=null && pp.length>=14) {
                int target = pp[0];
                int action = pp[1];
                int t = pp[2];
                int q = pp[3];
                int startQ = pp[4];
                int minQ = pp[5];
                int maxQ = pp[6];
                int dr = pp[7];
                int coding = pp[8];
                int p1 = pp[9];
                int Sel = pp[10];
                int Session = pp[11];
                int g = pp[12];
                int linkFrequency = pp[13];
                StringBuilder sb=new StringBuilder();
                sb.append("target=");sb.append(target);
                sb.append(" ,action=");sb.append(action);
                sb.append(" ,t=");sb.append(t);
                sb.append(" ,q=");sb.append(q);
                sb.append(" startQ=");sb.append(startQ);
                sb.append(" minQ=");sb.append(minQ);
                sb.append(" maxQ=");sb.append(maxQ);
                sb.append(" dr=");sb.append(dr);
                sb.append(" coding=");sb.append(coding);
                sb.append(" p=");sb.append(p1);
                sb.append(" Sel=");sb.append(Sel);
                sb.append(" Session=");sb.append(Session);
                sb.append(" g=");sb.append(g);
                sb.append(" linkFrequency=");sb.append(linkFrequency);
                Log.i("Session",sb.toString());
                SpSession.setSelection(Session);
                SpInv.setSelection(g);
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_Session_Success);
            }
            else
                UIHelper.ToastMessage(mContext,
                        R.string.uhf_msg_Session_Fail);
		}
		
		}
	public class SetFreOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			// byte[] bBaseFre = new byte[2];
			//
			// if (mContext.mReader.setFrequency(
			// (byte) spMode.getSelectedItemPosition(), (byte) 0,
			// bBaseFre, (byte) 0, (byte) 0, (byte) 0)) {
			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_set_frequency_succ);
			// } else {
			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_set_frequency_fail);
			// }

			if (mContext.mReader.setFrequencyMode((byte) spMode
					.getSelectedItemPosition())) {
				UIHelper.ToastMessage(mContext,
						R.string.uhf_msg_set_frequency_succ);
			} else {
				UIHelper.ToastMessage(mContext,
						R.string.uhf_msg_set_frequency_fail);
			}

		}
	}

	public void getFre() {
		int idx = mContext.mReader.getFrequencyMode();

		if (idx != -1) {
			int count = spMode.getCount();
			spMode.setSelection(idx > count - 1 ? count - 1 : idx);

			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_read_frequency_succ);
		} else {
			UIHelper.ToastMessage(mContext,
					R.string.uhf_msg_read_frequency_fail);
		}
	}

	public void getPwm() {
		int[] pwm = mContext.mReader.getPwm();

		if (pwm == null||pwm.length<2) {
			UIHelper.ToastMessage(mContext, R.string.uhf_msg_read_pwm_fail);
			return;
		}

		et_worktime.setText(pwm[0] + "");
		et_waittime.setText(pwm[1] + "");

	}

	public class SetPWMOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (mContext.mReader.setPwm(StringUtility.string2Int(et_worktime.getText().toString(),0), 
					StringUtility.string2Int(et_waittime.getText().toString(),0))) {
				UIHelper.ToastMessage(mContext,
						R.string.uhf_msg_set_pwm_succ);
			} else {
				UIHelper.ToastMessage(mContext,
						R.string.uhf_msg_set_pwm_fail);
			}
		}
	}

	public class GetFreOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			// String strFrequency = mContext.mReader.getFrequency();
			//
			// if (StringUtils.isNotEmpty(strFrequency)) {
			//
			// etFreRange.setText(strFrequency);
			//
			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_read_frequency_succ);
			//
			// } else {
			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_read_frequency_fail);
			// }

			getFre();
		}

	}

	@OnClick(R.id.btnGetPower)
	public void OnClick_GetPower(View view) {
		int iPower = mContext.mReader.getPower();

		Log.i("UHFSetFragment", "OnClick_GetPower() iPower=" + iPower);

		if (iPower > -1) {
			int position = iPower - 5;
			int count = spPower.getCount();
			spPower.setSelection(position > count - 1 ? count - 1 : position);

			// UIHelper.ToastMessage(mContext,
			// R.string.uhf_msg_read_power_succ);

		} else {
			UIHelper.ToastMessage(mContext, R.string.uhf_msg_read_power_fail);
		}

	}

	@OnClick(R.id.btnSetPower)
	public void OnClick_SetPower(View view) {
		int iPower = spPower.getSelectedItemPosition() + 5;

		Log.i("UHFSetFragment", "OnClick_SetPower() iPower=" + iPower);

		if (mContext.mReader.setPower(iPower)) {

			UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_power_succ);
		} else {
			UIHelper.ToastMessage(mContext, R.string.uhf_msg_set_power_fail);
		}

	}

}

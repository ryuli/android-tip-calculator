package org.example.tipcalculator;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class TipCalculatorActivity extends Activity {

	private TipHelper tipHelper;
	private EditText etTransAmount;
	private TextView tvCustomRatio;
	private TextView tvSplitBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);
        initUiComponent();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	try {
			tipHelper.saveCustomSetting();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void initUiComponent() {
    	tipHelper = new TipHelper(this);
    	
        tvCustomRatio = (TextView) findViewById(R.id.tvCustomRatio);
        tvSplitBy = (TextView) findViewById(R.id.tvSplitBy);
        
        etTransAmount = (EditText) findViewById(R.id.etTransAmount);
        etTransAmount.addTextChangedListener(new AmountChangeListener());
        
        Button btn; 
        btn = (Button) findViewById(R.id.btnRatio1);
        btn.setOnClickListener(new ButtonRatioListener());
        btn = (Button) findViewById(R.id.btnRatio2);
        btn.setOnClickListener(new ButtonRatioListener());
        btn = (Button) findViewById(R.id.btnRatio3);
        btn.setOnClickListener(new ButtonRatioListener());
        
        SeekBar sbCustomRatio = (SeekBar) findViewById(R.id.sbCustomRatio);
        sbCustomRatio.setOnSeekBarChangeListener(new CustomSeekbarListener());
        
        SeekBar sbSplitBy = (SeekBar) findViewById(R.id.sbSplitBy);
        sbSplitBy.setOnSeekBarChangeListener(new CustomSeekbarListener());
        
        tipHelper.initUiSetting();
    }
    
    private class ButtonRatioListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			tipHelper.setCurrentBtnActivate(btn);
			tipHelper.updateCalResult(btn);
			tipHelper.updateCustomRatio((int) (tipHelper.getTipRatioByComponent(btn) * 100));
		}
    	
    }
    
    private class CustomSeekbarListener implements SeekBar.OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			int id = seekBar.getId();
			switch (id) {
			case R.id.sbCustomRatio:
				tipHelper.resetAllRatioBtn(progress);
				tipHelper.updateCalResult(seekBar);
				tvCustomRatio.setText(String.valueOf(progress));
				break;

			case R.id.sbSplitBy:
				int num = progress;
				if (progress < TipHelper.DEFAULT_SPLIT_NUM) {
					seekBar.setProgress(TipHelper.DEFAULT_SPLIT_NUM);
					num = TipHelper.DEFAULT_SPLIT_NUM;
				}
				tvSplitBy.setText(String.valueOf(num));
				tipHelper.updateCalResult(seekBar);
				
				break;
			default:
				break;
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
    	
    }
    
    private class AmountChangeListener implements TextWatcher {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			tipHelper.updateCalResult(null);
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {	
		}

		@Override
		public void afterTextChanged(Editable s) {	
		}
    	
    }
}

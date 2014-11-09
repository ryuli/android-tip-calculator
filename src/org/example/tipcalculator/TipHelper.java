package org.example.tipcalculator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class TipHelper {

	public static final int DEFAULT_SPLIT_NUM = 1;
	public static final int DEFAULT_PERCENTAGE = 10;
	public static final String KEY_FOR_PERCENTAGE_SETTING = "percentage";
	public static final String KEY_FOR_SPLIT_NUM_SETTING = "split_num";
	public static final String SETTING_FILE_NAME = "setting";

	private Context context;
	private double calRatio = 0;
	private int splitNum;
	private Button lastActionBtn;
	private Button currentActionBtn;
	private SeekBar sbCustomRatio;
	private SeekBar sbSplitBy;
	private TextView tvTotalWithTip;
	private TextView tvTotalWithTipPerPerson;
	private File fileDir;
	private File fileSetting;

	public TipHelper(Context context) {
		this.context = context;
		initHelper();
	}

	public double getTipRatioByComponent(View view) {
		int id = 0;
		if (view != null) {
			id = view.getId();
		}
		switch (id) {
		case R.id.btnRatio1:
		case R.id.btnRatio2:
		case R.id.btnRatio3:
			if (id == R.id.btnRatio1) {
				calRatio = 0.1;
			} else if (id == R.id.btnRatio2) {
				calRatio = 0.15;
			} else {
				calRatio = 0.2;
			}
			break;
		case R.id.sbCustomRatio:
			SeekBar seekbar = (SeekBar) view;
			double progress = (double) seekbar.getProgress();
			calRatio = progress / 100.00;
			break;
		default:
			break;
		}

		return calRatio;
	}

	public static double calculateTip(double ratio, double amount) {
		double tipAmount = amount * ratio;
		return tipAmount;
	}

	public static String formatAmount(double amount) {
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
		return nf.format(amount);
	}

	public void setCurrentBtnActivate(Button btn) {
		if (lastActionBtn != null) {
			lastActionBtn.setTextAppearance(context, R.style.btnDeactivate);
			lastActionBtn.setBackgroundResource(R.drawable.black_border);
		}
		currentActionBtn = btn;
		currentActionBtn.setPressed(true);
		currentActionBtn.setTextAppearance(context, R.style.btnActivate);
		currentActionBtn.setBackgroundResource(R.drawable.black_bg);
		lastActionBtn = currentActionBtn;
	}

	public void resetAllRatioBtn(int percentage) {
		if (currentActionBtn != null) {
			currentActionBtn.setTextAppearance(context, R.style.btnDeactivate);
			currentActionBtn.setBackgroundResource(R.drawable.black_border);
			currentActionBtn = null;
		}
		lastActionBtn = null;

		int id = 0;
		switch (percentage) {
		case 10:
			id = R.id.btnRatio1;
			break;
		case 15:
			id = R.id.btnRatio2;
			break;
		case 20:
			id = R.id.btnRatio3;
			break;
		default:
			break;
		}

		if (id != 0) {
			Button btn = (Button) ((Activity) context).findViewById(id);
			setCurrentBtnActivate(btn);
		}
	}

	public void updateCalResult(View view) {
		calRatio = getTipRatioByComponent(view);
		splitNum = getSplitNum();
		Activity activity = (Activity) context;
		EditText etTransAmount = (EditText) activity
				.findViewById(R.id.etTransAmount);
		String content = etTransAmount.getText().toString();
		double transAmount;
		if (content.equals("")) {
			transAmount = 0;
		} else {
			transAmount = Double.valueOf(content);
		}
		double tipAmount = TipHelper.calculateTip(calRatio, transAmount);
		double totalWithTip = transAmount + tipAmount;
		double totalWithTipPerPerson = totalWithTip / splitNum;
		tvTotalWithTip.setText(TipHelper.formatAmount(totalWithTip));
		tvTotalWithTipPerPerson.setText(TipHelper
				.formatAmount(totalWithTipPerPerson));
	}

	public void setcurrentActionBtn(Button btn) {
		currentActionBtn = btn;
	}

	public void updateCustomRatio(int progress) {
		SeekBar seekbar = (SeekBar) ((Activity) context)
				.findViewById(R.id.sbCustomRatio);
		seekbar.setProgress(progress);
	}

	public void saveCustomSetting() throws JSONException, IOException {
		JSONObject customSetting = new JSONObject();
		int percentage = (int) (calRatio * 100);
		customSetting.put(KEY_FOR_PERCENTAGE_SETTING, percentage);
		customSetting.put(KEY_FOR_SPLIT_NUM_SETTING, splitNum);
		String json = customSetting.toString();
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileSetting));
		writer.write(json);
		if (writer != null) {
			writer.close();
		}
	}

	public void initUiSetting() {
		JSONObject customSetting = null;
		int percentage_progress = DEFAULT_PERCENTAGE;
		int split_num_progress = DEFAULT_SPLIT_NUM;
		try {
			customSetting = readCustomSetting();
			if (customSetting != null) {
				if (!customSetting.isNull(KEY_FOR_PERCENTAGE_SETTING)) {
					percentage_progress = customSetting.getInt(KEY_FOR_PERCENTAGE_SETTING);
				}
				if (!customSetting.isNull(KEY_FOR_SPLIT_NUM_SETTING)) {
					split_num_progress = customSetting.getInt(KEY_FOR_SPLIT_NUM_SETTING);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			sbCustomRatio.setProgress(percentage_progress);
			sbSplitBy.setProgress(split_num_progress);
		}
	}

	private JSONObject readCustomSetting() throws IOException, JSONException {
		BufferedReader reader = new BufferedReader(new FileReader(fileSetting));

		StringBuffer content = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			content.append(line);
		}
		if (reader != null) {
			reader.close();
		}

		String json = content.toString();
		JSONObject customSetting = new JSONObject(json);

		return customSetting;
	}

	private void initHelper() {
		Activity activity = (Activity) context;
		sbCustomRatio = (SeekBar) activity.findViewById(R.id.sbCustomRatio);
		sbSplitBy = (SeekBar) activity.findViewById(R.id.sbSplitBy);
		tvTotalWithTip = (TextView) activity.findViewById(R.id.tvTotalWithTip);
		tvTotalWithTipPerPerson = (TextView) activity
				.findViewById(R.id.tvTotalWithTipPerPerson);

		fileDir = context.getFilesDir();
		fileSetting = new File(fileDir, SETTING_FILE_NAME);
	}

	private int getSplitNum() {
		return sbSplitBy.getProgress();
	}

}

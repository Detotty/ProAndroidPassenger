package com.fixeperts.customer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.androidquery.callback.ImageOptions;
import com.fixeperts.customer.component.MyTitleFontTextView;
import com.fixeperts.customer.fragments.UberBaseFragmentRegister;
import com.fixeperts.customer.parse.AsyncTaskCompleteListener;
import com.fixeperts.customer.utils.AppLog;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

/*import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;*/
//import com.savelog.SaveLog;

/**
 * @author Hardik A Bhalodi
 */
@SuppressLint("NewApi")
abstract public class ActionBarBaseActivitiy extends ActionBarActivity
		implements OnClickListener, AsyncTaskCompleteListener, ErrorListener {

	public ActionBar actionBar;
	private int mFragmentId = 0;
	private String mFragmentTag = null;
	public ImageButton btnNotification, btnActionMenu;
	public MyTitleFontTextView tvTitle;
	public AutoCompleteTextView etSource;
	public String currentFragment = null;
	public LinearLayout layoutDestination;
	public ImageButton imgClearDst;
	//public int paymentMode = 0;
	private Button btnPay;
	//set the environment for production/sandbox/no netowrk
	private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    private static final String CONFIG_CLIENT_ID = "AaOjXJSRV_atSrOaPWVStkMa7LeIa3JxrpBJRB9w_nehJS6bHrjD7fsA3P8HiDRwPsZHltseC62Swq_k";

    private static final int REQUEST_PAYPAL_PAYMENT = 111;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Pro Taxi")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

	protected abstract boolean isValidate();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getSupportActionBar();
		//SaveLog.getInstance(this);
		// Custom Action Bar
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View customActionBarView = inflater.inflate(R.layout.custom_action_bar,
				null);
		layoutDestination = (LinearLayout) customActionBarView
				.findViewById(R.id.layoutDestination);
		btnNotification = (ImageButton) customActionBarView
				.findViewById(R.id.btnActionNotification);
		btnNotification.setOnClickListener(this);

		imgClearDst = (ImageButton) customActionBarView
				.findViewById(R.id.imgClearDst);

		tvTitle = (MyTitleFontTextView) customActionBarView
				.findViewById(R.id.tvTitle);
		tvTitle.setOnClickListener(this);

		etSource = (AutoCompleteTextView) customActionBarView
				.findViewById(R.id.etEnterSouce);

		btnActionMenu = (ImageButton) customActionBarView
				.findViewById(R.id.btnActionMenu);
		btnActionMenu.setOnClickListener(this);
		try {
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
					ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
							| ActionBar.DISPLAY_SHOW_TITLE);
			actionBar.setCustomView(customActionBarView,
					new ActionBar.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.MATCH_PARENT));
		} catch (Exception e) {
			e.printStackTrace();
		}

		findViewById(R.id.btnShare).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent
						.putExtra(
								Intent.EXTRA_TEXT,
								"I am using "
										+ getString(R.string.app_name)
										+ " App ! Why don't you try it out...\nInstall "
										+ getString(R.string.app_name)
										+ " now !\nhttps://play.google.com/store/apps/details?id="
										+ getPackageName());
				sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						getString(R.string.app_name) + " App !");
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent,
						getString(R.string.text_share_app)));
			}
		});
	}

	public void setFbTag(String tag) {
		mFragmentId = 0;
		mFragmentTag = tag;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		
		 try {
			Fragment fragment = null;

			if (mFragmentId > 0) {
				fragment = getSupportFragmentManager()
						.findFragmentById(mFragmentId);
			}
			
			else if (mFragmentTag != null && !mFragmentTag.equalsIgnoreCase("")) {
				fragment = getSupportFragmentManager().findFragmentByTag(
						mFragmentTag);
			}
			
			if (fragment != null) {
				fragment.onActivityResult(requestCode, resultCode, data);
			}
			
			 /*if (requestCode == REQUEST_PAYPAL_PAYMENT) {
				  super.onActivityResult(requestCode, resultCode, data);
				
				  try {
					if (resultCode == Activity.RESULT_OK) {
					        PaymentConfirmation confirm = data
					                .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
					        if (confirm != null) {
					            try {
					            	//System.out.println("Responseeee"+confirm);
					                Log.i("paymentExample", confirm.toJSONObject().toString());

					              
					                JSONObject jsonObj=new JSONObject(confirm.toJSONObject().toString());
					                
					                String paymentId=jsonObj.getJSONObject("response").getString("id");
					                //System.out.println("payment id:-=="+paymentId);
					                Toast.makeText(getApplicationContext(), "Payment Successful With ID- "+paymentId, Toast.LENGTH_LONG).show();  

					            } 
					            
					            catch (JSONException e) {
					                Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
					            }
					        }
					    } 
					     else if (resultCode == Activity.RESULT_CANCELED) {
					        Log.i("paymentExample", "The user canceled.");
					    } 
					      else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
					        Log.i("paymentExample", "An invalid Payment was submitted. Please see the docs.");
					    }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.getMessage();
				}
			   }*/
			 
			 
	/*		 FragmentManager manager = getSupportFragmentManager();
				UberBaseFragmentRegister frag = ((UberBaseFragmentRegister) manager
						.findFragmentByTag(currentFragment));
				
				frag.onActivityResult(requestCode, resultCode, data);*/
			
			 /*  List<Fragment> fragments = getSupportFragmentManager().findFragmentByTag(0x7f080071);
		            for (Fragment fragmentm : fragments) {
		                fragmentm.onActivityResult(requestCode, resultCode, data);
		                break;
		            }
		        }
		        */
			
			 //fragment.onActivityResult(requestCode, resultCode, data);

		        /*List<Fragment> fragments = getSupportFragmentManager().getFragments();
		        if (fragments != null) {
		            for (Fragment fragmentm : fragments) {
		                fragmentm.onActivityResult(requestCode, resultCode, data);
		            }
		        }*/
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
		
		
	}

	public void startActivityForResult(Intent intent, int requestCode,
			int fragmentId) {
		mFragmentId = fragmentId;
		mFragmentTag = null;
		super.startActivityForResult(intent, requestCode);
	}

	public void startActivityForResult(Intent intent, int requestCode,
			String fragmentTag) {
		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startActivityForResult(intent, requestCode);
	}

	public void startActivityForResult(Intent intent, int requestCode,
			int fragmentId, Bundle options) {

		mFragmentId = fragmentId;
		mFragmentTag = null;
		super.startActivityForResult(intent, requestCode, options);
	}

	public void startActivityForResult(Intent intent, int requestCode,
			String fragmentTag, Bundle options) {

		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startActivityForResult(intent, requestCode, options);
	}

	public void startIntentSenderForResult(Intent intent, int requestCode,
			String fragmentTag, Bundle options) {

		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startActivityForResult(intent, requestCode, options);
	}

	@Override
	@Deprecated
	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags) throws SendIntentException {
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags);
	}

	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags, String fragmentTag)
			throws SendIntentException {

		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags);
	}

	@Override
	@Deprecated
	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags, Bundle options)
			throws SendIntentException {
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags, options);
	}

	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags, Bundle options, String fragmentTag)
			throws SendIntentException {
		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags, options);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode,
			Bundle options) {
		super.startActivityForResult(intent, requestCode, options);
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {

	}

	public void addFragment(Fragment fragment, boolean addToBackStack,
			String tag) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
				R.anim.slide_in_left, R.anim.slide_out_right);
		if (addToBackStack) {
			ft.addToBackStack(tag);
		}
		ft.replace(R.id.content_frame, fragment, tag);
		ft.commitAllowingStateLoss();
	}

	public void addFragment(Fragment fragment, boolean addToBackStack,
			boolean isAnimate, String tag) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		if (isAnimate)
			ft.setCustomAnimations(R.anim.slide_in_right,
					R.anim.slide_out_left, R.anim.slide_in_left,
					R.anim.slide_out_right);
		if (addToBackStack) {
			ft.addToBackStack(tag);
		}
		ft.replace(R.id.content_frame, fragment, tag);
		ft.commitAllowingStateLoss();
	}

	public void addFragmentWithStateLoss(Fragment fragment,
			boolean addToBackStack, String tag) {

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();

		if (addToBackStack) {
			ft.addToBackStack(tag);
		}
		ft.replace(R.id.content_frame, fragment, tag);
		ft.commitAllowingStateLoss();
	}

	public void removeAllFragment(Fragment replaceFragment,
			boolean addToBackStack, String tag) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();

		manager.popBackStackImmediate(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		if (addToBackStack) {
			ft.addToBackStack(tag);
		}
		ft.replace(R.id.content_frame, replaceFragment);
		ft.commit();
	}

	public void clearBackStackImmidiate() {

		FragmentManager manager = getSupportFragmentManager();

		manager.popBackStackImmediate(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);

	}

	public void clearBackStack() {
		FragmentManager manager = getSupportFragmentManager();
		if (manager.getBackStackEntryCount() > 0) {
			FragmentManager.BackStackEntry first = manager
					.getBackStackEntryAt(0);
			manager.popBackStack(first.getId(),
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (!TextUtils.isEmpty(currentFragment)) {
			FragmentManager manager = getSupportFragmentManager();
			UberBaseFragmentRegister frag = ((UberBaseFragmentRegister) manager
					.findFragmentByTag(currentFragment));

			if (frag != null && frag.isVisible())
				frag.OnBackPressed();
			else
				super.onBackPressed();
		} else {
			super.onBackPressed();

		}
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		default:
			break;
		}
		return true;
	}

	protected ImageOptions getAqueryOption() {
		ImageOptions options = new ImageOptions();
		options.targetWidth = 200;
		options.memCache = true;
		options.fallback = R.drawable.default_user;
		options.fileCache = true;
		return options;
	}

	public void openExitDialog() {
		final Dialog mDialog = new Dialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.exit_layout);
		mDialog.findViewById(R.id.tvExitOk).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
						finish();
						overridePendingTransition(R.anim.slide_in_left,
								R.anim.slide_out_right);
					}
				});
		mDialog.findViewById(R.id.tvExitCancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDialog.dismiss();
					}
				});
		mDialog.show();
	}
	
	
	public void showBillDialogWallet(String timeCost, String total, String distCost,String basePrice, String time, String distance, String promoBouns,
			String referralBouns, String btnTitle,int paymentMode) {
		
		final Dialog mDialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.bill_layout);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		DecimalFormat perHourFormat = new DecimalFormat("0.0");
		//
		String basePricetmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(basePrice)));
		final String totalTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(total)));
		String distCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(distCost)));
		String timeCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(timeCost)));

		AppLog.Log("Distacne", distance);
		AppLog.Log("Time", time);

		((TextView) mDialog.findViewById(R.id.tvBasePrice)).setText(basePrice);
		if (Double.parseDouble(distCost) != 0) {
			((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
					.setText(String.valueOf(perHourFormat.format((Double
							.parseDouble(distCost) / Double
							.parseDouble(distance))))
							+ getResources().getString(
									R.string.text_cost_per_mile));
		} else {
			((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
					.setText(String.valueOf(perHourFormat.format(0.00))
							+ getResources().getString(
									R.string.text_cost_per_mile));
		}
		if (Double.parseDouble(timeCost) != 0) {
			((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
					.setText(String.valueOf(perHourFormat.format((Double
							.parseDouble(timeCost) / Double.parseDouble(time))))
							+ getResources().getString(
									R.string.text_cost_per_hour));
		} else {
			((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
					.setText(String.valueOf(perHourFormat.format((0.00)))
							+ getResources().getString(
									R.string.text_cost_per_hour));
		}
		
		((TextView) mDialog.findViewById(R.id.tvDis1)).setText(distCostTmp);

		((TextView) mDialog.findViewById(R.id.tvTime1)).setText(timeCostTmp);

		((TextView) mDialog.findViewById(R.id.tvTotal1)).setText(totalTmp);
		((TextView) mDialog.findViewById(R.id.tvPromoBonus))
				.setText(decimalFormat.format(Double.parseDouble(promoBouns)));
		((TextView) mDialog.findViewById(R.id.tvReferralBonus))
				.setText(decimalFormat.format(Double.parseDouble(referralBouns)));

		Button btnConfirm = (Button) mDialog.findViewById(R.id.btnBillDialogClose);
		
		if(paymentMode==1){
			
			if (!TextUtils.isEmpty(btnTitle)) {
				btnConfirm.setText(btnTitle);
			}
			
			btnConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
						mDialog.dismiss();
				}
			});
			
		}
		else{
				btnConfirm.setText("Pay by PayPal");
			
			btnConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(totalTmp),"USD", "Pro Taxi",
				            PayPalPayment.PAYMENT_INTENT_SALE);

				        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);

				        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

				        startActivityForResult(intent, REQUEST_PAYPAL_PAYMENT);	
				        mDialog.dismiss();	
				        
				}
			});
			
			Intent intent = new Intent(this, PayPalService.class);
	        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
	        startService(intent);
	        
		}
		
	

		mDialog.setCancelable(true);
		mDialog.show();
	}
	
	
	
	

	public void showBillDialog(String timeCost, String total, String distCost,String basePrice, String time, String distance, String promoBouns,
			String referralBouns, String btnTitle) {
		final Dialog mDialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.bill_layout);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		DecimalFormat perHourFormat = new DecimalFormat("0.0");
		//
		String basePricetmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(basePrice)));
		String totalTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(total)));
		String distCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(distCost)));
		String timeCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(timeCost)));

		AppLog.Log("Distacne", distance);
		AppLog.Log("Time", time);

		((TextView) mDialog.findViewById(R.id.tvBasePrice)).setText(basePrice);
		if (Double.parseDouble(distCost) != 0) {
			((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
					.setText(String.valueOf(perHourFormat.format((Double
							.parseDouble(distCost) / Double
							.parseDouble(distance))))
							+ getResources().getString(
									R.string.text_cost_per_mile));
		} else {
			((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
					.setText(String.valueOf(perHourFormat.format(0.00))
							+ getResources().getString(
									R.string.text_cost_per_mile));
		}
		if (Double.parseDouble(timeCost) != 0) {
			((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
					.setText(String.valueOf(perHourFormat.format((Double
							.parseDouble(timeCost) / Double.parseDouble(time))))
							+ getResources().getString(
									R.string.text_cost_per_hour));
		} else {
			((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
					.setText(String.valueOf(perHourFormat.format((0.00)))
							+ getResources().getString(
									R.string.text_cost_per_hour));
		}
		((TextView) mDialog.findViewById(R.id.tvDis1)).setText(distCostTmp);

		((TextView) mDialog.findViewById(R.id.tvTime1)).setText(timeCostTmp);

		((TextView) mDialog.findViewById(R.id.tvTotal1)).setText(totalTmp);
		((TextView) mDialog.findViewById(R.id.tvPromoBonus))
				.setText(decimalFormat.format(Double.parseDouble(promoBouns)));
		((TextView) mDialog.findViewById(R.id.tvReferralBonus))
				.setText(decimalFormat.format(Double.parseDouble(referralBouns)));

		Button btnConfirm = (Button) mDialog.findViewById(R.id.btnBillDialogClose);
		if (!TextUtils.isEmpty(btnTitle)) {
			btnConfirm.setText(btnTitle);
		}
		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

		mDialog.setCancelable(true);
		mDialog.show();
	}

	public void setTitle(String str) {
		tvTitle.setText(str);
	}

	public void setIconMenu(int img) {
		btnActionMenu.setImageResource(img);
	}

	public void setIcon(int img) {
		btnNotification.setImageResource(img);
	}

	public void goToMainActivity() {
		Intent i = new Intent(this, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
		finish();
	}
	public void clearAll() {
		NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.cancelAll();
	}
	
	
	
	
}

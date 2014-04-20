package com.screem_control;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.screem_control.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class CallActivity extends Activity {
	/** Called when the activity is first created. */
	ListView listView;
	AutoCompleteTextView textView;
	TextView emptytextView;
	protected CursorAdapter mCursorAdapter;
	protected Cursor mCursor = null;
	protected ContactAdapter2 ca2;
	ArrayList<ContactInfo> contactList = new ArrayList<ContactInfo>();
	// 选中的手机号
	protected String numberStr = "";
	protected String[] autoContact = null;
	protected String[] wNumStr = null;
	private static final int DIALOG_KEY = 0;
	private Button AddContacts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// 自定义标题栏
		listView = (ListView) findViewById(R.id.list);
		textView = (AutoCompleteTextView) findViewById(R.id.edit);
		emptytextView = (TextView) findViewById(R.id.empty);
		Button btn_add = (Button) findViewById(R.id.btn_add);
		Button btn_back = (Button) findViewById(R.id.btn_back);
		AddContacts = (Button) findViewById(R.id.AddContacts);

		AddContacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 此处是新增加的弹出式对话框
				final LinearLayout serverviewLayout = (LinearLayout) getLayoutInflater()
						.inflate(R.layout.addcontactlayout, null);
				new AlertDialog.Builder(CallActivity.this)
						.setTitle("请输入联系人姓名和电话")
						.setView(serverviewLayout)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 这里如果要拿到XML中的对话框的话仅仅用findviewbyid是不行的，在之前要使用开始得到的VIEW，例如此题应当：
										// 姓名
										EditText NameEdit = (EditText) serverviewLayout
												.findViewById(R.id.contactName);
										// 电话
										EditText TelEdit = (EditText) serverviewLayout
												.findViewById(R.id.contactTel);

										Log.v("添加", "loading");
										insertContact(CallActivity.this,
												NameEdit.getText().toString(),
												TelEdit.getText().toString());
										Log.v("添加成功", "success");
										Toast.makeText(CallActivity.this,
												"添加联系人成功！", Toast.LENGTH_SHORT)
												.show();

									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).show();
			}
		});

		emptytextView.setVisibility(View.GONE);

		// 获取前页传值,如果有手动填写的手机号在通讯录中,则默认勾中
		// 如果手动填写的手机号不在通讯录中,则在回传值的时候带回去(不符合手机格式的去除)
		wNumStr = new String[] { "123", "456" };
		// 启动进程
		new GetContactTask().execute("");

		// 列表点击事件监听
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 打电话的功能
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ contactList.get(position).getUserNumber()));
				CallActivity.this.startActivity(intent);
			}
		});

		btn_add.setOnClickListener(btnClick);
		btn_back.setOnClickListener(btnClick);
	}

	// 按钮监听
	private OnClickListener btnClick = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_add:
				// 打电话的功能
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ textView.getText().toString()));
				CallActivity.this.startActivity(intent);
				break;
			case R.id.btn_back:
				finish();
				break;
			}
		}
	};

	// 监听AUTOTEXT内容变化,当出现符合选中联系人[联系人(手机号)]的情况下,将该勾中,并加入选中手机号中
	private TextWatcher mTextWatcher = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int before,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int after) {

			String autoText = s.toString();
			if (autoText.length() >= 13) {
				Pattern pt = Pattern.compile("\\(([1][3,5,8]+\\d{9})\\)");
				Matcher mc = pt.matcher(autoText);
				if (mc.find()) {
					String sNumber = mc.group(1);
					DealWithAutoComplete(contactList, sNumber);
					// 打电话的功能
					Intent intent = new Intent(Intent.ACTION_CALL,
							Uri.parse("tel:" + sNumber));
					CallActivity.this.startActivity(intent);

					ca2.setItemList(contactList);
					ca2.notifyDataSetChanged();
				}
			}
		}

		public void afterTextChanged(Editable s) {
		}

	};

	// 获取通讯录进程
	private class GetContactTask extends AsyncTask<String, String, String> {
		public String doInBackground(String... params) {

			// 从本地手机中获取
			GetLocalContact();
			// 从SIM卡中获取
			GetSimContact("content://icc/adn");
			// 发现有得手机的SIM卡联系人在这个路径...所以都取了(每次验证是否已存在)
			GetSimContact("content://sim/adn");
			return "";
		}

		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_KEY);
		}

		@Override
		public void onPostExecute(String Re) {
			// 绑定LISTVIEW
			if (contactList.size() == 0) {
				emptytextView.setVisibility(View.VISIBLE);
			} else {
				// 按中文拼音顺序排序
				Comparator<Object> comp = new Mycomparator();
				Collections.sort(contactList, comp);

				numberStr = GetNotInContactNumber(wNumStr, contactList)
						+ numberStr;
				ca2 = new ContactAdapter2(CallActivity.this, contactList);
				listView.setAdapter(ca2);
				listView.setTextFilterEnabled(true);
				// 编辑AUTOCOMPLETE数组
				autoContact = new String[contactList.size()];
				for (int c = 0; c < contactList.size(); c++) {
					autoContact[c] = contactList.get(c).contactName + "("
							+ contactList.get(c).userNumber + ")";
				}
				// 绑定AUTOCOMPLETE
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						CallActivity.this,
						android.R.layout.simple_dropdown_item_1line,
						autoContact);
				textView.setAdapter(adapter);
				textView.addTextChangedListener(mTextWatcher);
			}
			removeDialog(DIALOG_KEY);
		}
	}

	// 弹出"查看"对话框
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_KEY: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("获取通讯录中...请稍候");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		}
		}
		return null;
	}

	// 从本机中取号
	private void GetLocalContact() {
		// 得到ContentResolver对象
		ContentResolver cr = getContentResolver();
		// 取得电话本中开始一项的光标
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		while (cursor.moveToNext()) {
			ContactInfo cci = new ContactInfo();
			// 取得联系人名字
			int nameFieldColumnIndex = cursor
					.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			cci.contactName = cursor.getString(nameFieldColumnIndex);

			// 取得联系人ID
			int id = cursor.getInt(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phone = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
					new String[] { Integer.toString(id) }, null);// 再类ContactsContract.CommonDataKinds.Phone中根据查询相应id联系人的所有电话；

			// 取得电话号码(可能存在多个号码)
			while (phone.moveToNext()) {
				String strPhoneNumber = phone
						.getString(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				System.out.println(strPhoneNumber);
				cci.userNumber = GetNumber(strPhoneNumber);
				cci.isChecked = false;

				if (!IsContain(contactList, cci.userNumber)) {
					if (IsUserNumber(strPhoneNumber)) {
						if (IsAlreadyCheck(wNumStr, cci.userNumber)) {
							cci.isChecked = true;
							numberStr += "," + cci.userNumber;
						}
						contactList.add(cci);
					}
				}
			}
			phone.close();
		}
		cursor.close();
	}

	// 从SIM卡中取号
	private void GetSimContact(String add) {
		// 读取SIM卡手机号,有两种可能:content://icc/adn与content://sim/adn
		try {
			Intent intent = new Intent();
			intent.setData(Uri.parse(add));
			Uri uri = intent.getData();
			mCursor = getContentResolver().query(uri, null, null, null, null);
			if (mCursor != null) {
				while (mCursor.moveToNext()) {
					ContactInfo sci = new ContactInfo();
					// 取得联系人名字
					int nameFieldColumnIndex = mCursor.getColumnIndex("name");
					sci.contactName = mCursor.getString(nameFieldColumnIndex);

					// 取得电话号码
					int numberFieldColumnIndex = mCursor
							.getColumnIndex("number");
					sci.userNumber = mCursor.getString(numberFieldColumnIndex);

					sci.userNumber = GetNumber(sci.userNumber);
					sci.isChecked = false;

					if (IsUserNumber(sci.userNumber)) {// 是否是手机号码
						if (!IsContain(contactList, sci.userNumber)) {// //是否在LIST有值
							if (IsAlreadyCheck(wNumStr, sci.userNumber)) {// 手输手机号的是否在通讯录中
								sci.isChecked = true;
								numberStr += "," + sci.userNumber;
							}
							contactList.add(sci);
							// Log.i("eoe", "*********"+sci.userNumber);
						}
					}
				}
				mCursor.close();
			}
		} catch (Exception e) {
			Log.i("eoe", e.toString());
		}
	}

	// 是否在LIST有值
	private boolean IsContain(ArrayList<ContactInfo> list, String un) {
		for (int i = 0; i < list.size(); i++) {
			if (un.equals(list.get(i).userNumber)) {
				return true;
			}
		}
		return false;
	}

	// 手输手机号的是否在通讯录中
	private boolean IsAlreadyCheck(String[] Str, String un) {
		if (Str == null) {
			return false;
		}
		for (int i = 0; i < Str.length; i++) {
			if (un.equals(Str[i])) {
				return true;
			}
		}
		return false;
	}

	// 获取手输手机号不在通讯录中的号码
	private String GetNotInContactNumber(String[] Str,
			ArrayList<ContactInfo> list) {
		String re = "";
		for (int i = 0; i < Str.length; i++) {
			if (IsUserNumber(Str[i])) {
				for (int l = 0; l < list.size(); l++) {
					if (Str[i].equals(list.get(l).userNumber)) {
						Str[i] = "";
						break;
					}
				}
				if (!Str[i].equals("")) {
					re += "," + Str[i];
				}
			}
		}
		return re;
	}

	// 处理搜索框选中的手机号
	private void DealWithAutoComplete(ArrayList<ContactInfo> list, String un) {
		for (int i = 0; i < list.size(); i++) {
			if (un.equals(list.get(i).userNumber)) {
				if (!list.get(i).isChecked) {
					list.get(i).isChecked = true;
					numberStr += "," + un;
					textView.setText("");
				}
			}
		}
	}

	// 通讯录按中文拼音排序
	public class Mycomparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			ContactInfo c1 = (ContactInfo) o1;
			ContactInfo c2 = (ContactInfo) o2;
			Comparator<Object> cmp = Collator.getInstance(java.util.Locale.CHINA);
			return cmp.compare(c1.contactName, c2.contactName);
		}

	}

	// 是否为手机号码 有的通讯录格式为135-1568-1234
	public static boolean IsUserNumber(String num) {
		boolean re = false;
		if (num.length() >= 11) {
			if (num.startsWith("1")) {
				re = true;
			}

		}
		return re;
	}

	// 还原11位手机号 包括去除“-”
	public static String GetNumber(String num2) {
		String num;
		if (num2 != null) {
			System.out.println(num2);
			num = num2.replaceAll("-", "");
			if (num.startsWith("+86")) {
				num = num.substring(3);
			} else if (num.startsWith("86")) {
				num = num.substring(2);
			} else if (num.startsWith("86")) {
				num = num.substring(2);
			}
		} else {
			num = "";
		}
		return num;
	}

	// 往通讯录中增加联系人的方法
	private Uri insertContact(Context context, String name, String phone) {

		ContentValues values = new ContentValues();
		values.put(People.NAME, name);
		Uri uri = getContentResolver().insert(People.CONTENT_URI, values);
		Uri numberUri = Uri.withAppendedPath(uri,
				People.Phones.CONTENT_DIRECTORY);
		values.clear();

		values.put(Contacts.Phones.TYPE, People.Phones.TYPE_MOBILE);
		values.put(People.NUMBER, phone);
		getContentResolver().insert(numberUri, values);

		return uri;
	}
}

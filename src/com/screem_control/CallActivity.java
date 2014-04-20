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
	// ѡ�е��ֻ���
	protected String numberStr = "";
	protected String[] autoContact = null;
	protected String[] wNumStr = null;
	private static final int DIALOG_KEY = 0;
	private Button AddContacts;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// �Զ��������
		listView = (ListView) findViewById(R.id.list);
		textView = (AutoCompleteTextView) findViewById(R.id.edit);
		emptytextView = (TextView) findViewById(R.id.empty);
		Button btn_add = (Button) findViewById(R.id.btn_add);
		Button btn_back = (Button) findViewById(R.id.btn_back);
		AddContacts = (Button) findViewById(R.id.AddContacts);

		AddContacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �˴��������ӵĵ���ʽ�Ի���
				final LinearLayout serverviewLayout = (LinearLayout) getLayoutInflater()
						.inflate(R.layout.addcontactlayout, null);
				new AlertDialog.Builder(CallActivity.this)
						.setTitle("��������ϵ�������͵绰")
						.setView(serverviewLayout)
						.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// �������Ҫ�õ�XML�еĶԻ���Ļ�������findviewbyid�ǲ��еģ���֮ǰҪʹ�ÿ�ʼ�õ���VIEW���������Ӧ����
										// ����
										EditText NameEdit = (EditText) serverviewLayout
												.findViewById(R.id.contactName);
										// �绰
										EditText TelEdit = (EditText) serverviewLayout
												.findViewById(R.id.contactTel);

										Log.v("���", "loading");
										insertContact(CallActivity.this,
												NameEdit.getText().toString(),
												TelEdit.getText().toString());
										Log.v("��ӳɹ�", "success");
										Toast.makeText(CallActivity.this,
												"�����ϵ�˳ɹ���", Toast.LENGTH_SHORT)
												.show();

									}
								})
						.setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).show();
			}
		});

		emptytextView.setVisibility(View.GONE);

		// ��ȡǰҳ��ֵ,������ֶ���д���ֻ�����ͨѶ¼��,��Ĭ�Ϲ���
		// ����ֶ���д���ֻ��Ų���ͨѶ¼��,���ڻش�ֵ��ʱ�����ȥ(�������ֻ���ʽ��ȥ��)
		wNumStr = new String[] { "123", "456" };
		// ��������
		new GetContactTask().execute("");

		// �б����¼�����
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ��绰�Ĺ���
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ contactList.get(position).getUserNumber()));
				CallActivity.this.startActivity(intent);
			}
		});

		btn_add.setOnClickListener(btnClick);
		btn_back.setOnClickListener(btnClick);
	}

	// ��ť����
	private OnClickListener btnClick = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_add:
				// ��绰�Ĺ���
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

	// ����AUTOTEXT���ݱ仯,�����ַ���ѡ����ϵ��[��ϵ��(�ֻ���)]�������,���ù���,������ѡ���ֻ�����
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
					// ��绰�Ĺ���
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

	// ��ȡͨѶ¼����
	private class GetContactTask extends AsyncTask<String, String, String> {
		public String doInBackground(String... params) {

			// �ӱ����ֻ��л�ȡ
			GetLocalContact();
			// ��SIM���л�ȡ
			GetSimContact("content://icc/adn");
			// �����е��ֻ���SIM����ϵ�������·��...���Զ�ȡ��(ÿ����֤�Ƿ��Ѵ���)
			GetSimContact("content://sim/adn");
			return "";
		}

		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_KEY);
		}

		@Override
		public void onPostExecute(String Re) {
			// ��LISTVIEW
			if (contactList.size() == 0) {
				emptytextView.setVisibility(View.VISIBLE);
			} else {
				// ������ƴ��˳������
				Comparator<Object> comp = new Mycomparator();
				Collections.sort(contactList, comp);

				numberStr = GetNotInContactNumber(wNumStr, contactList)
						+ numberStr;
				ca2 = new ContactAdapter2(CallActivity.this, contactList);
				listView.setAdapter(ca2);
				listView.setTextFilterEnabled(true);
				// �༭AUTOCOMPLETE����
				autoContact = new String[contactList.size()];
				for (int c = 0; c < contactList.size(); c++) {
					autoContact[c] = contactList.get(c).contactName + "("
							+ contactList.get(c).userNumber + ")";
				}
				// ��AUTOCOMPLETE
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

	// ����"�鿴"�Ի���
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_KEY: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("��ȡͨѶ¼��...���Ժ�");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		}
		}
		return null;
	}

	// �ӱ�����ȡ��
	private void GetLocalContact() {
		// �õ�ContentResolver����
		ContentResolver cr = getContentResolver();
		// ȡ�õ绰���п�ʼһ��Ĺ��
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		while (cursor.moveToNext()) {
			ContactInfo cci = new ContactInfo();
			// ȡ����ϵ������
			int nameFieldColumnIndex = cursor
					.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			cci.contactName = cursor.getString(nameFieldColumnIndex);

			// ȡ����ϵ��ID
			int id = cursor.getInt(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phone = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
					new String[] { Integer.toString(id) }, null);// ����ContactsContract.CommonDataKinds.Phone�и��ݲ�ѯ��Ӧid��ϵ�˵����е绰��

			// ȡ�õ绰����(���ܴ��ڶ������)
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

	// ��SIM����ȡ��
	private void GetSimContact(String add) {
		// ��ȡSIM���ֻ���,�����ֿ���:content://icc/adn��content://sim/adn
		try {
			Intent intent = new Intent();
			intent.setData(Uri.parse(add));
			Uri uri = intent.getData();
			mCursor = getContentResolver().query(uri, null, null, null, null);
			if (mCursor != null) {
				while (mCursor.moveToNext()) {
					ContactInfo sci = new ContactInfo();
					// ȡ����ϵ������
					int nameFieldColumnIndex = mCursor.getColumnIndex("name");
					sci.contactName = mCursor.getString(nameFieldColumnIndex);

					// ȡ�õ绰����
					int numberFieldColumnIndex = mCursor
							.getColumnIndex("number");
					sci.userNumber = mCursor.getString(numberFieldColumnIndex);

					sci.userNumber = GetNumber(sci.userNumber);
					sci.isChecked = false;

					if (IsUserNumber(sci.userNumber)) {// �Ƿ����ֻ�����
						if (!IsContain(contactList, sci.userNumber)) {// //�Ƿ���LIST��ֵ
							if (IsAlreadyCheck(wNumStr, sci.userNumber)) {// �����ֻ��ŵ��Ƿ���ͨѶ¼��
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

	// �Ƿ���LIST��ֵ
	private boolean IsContain(ArrayList<ContactInfo> list, String un) {
		for (int i = 0; i < list.size(); i++) {
			if (un.equals(list.get(i).userNumber)) {
				return true;
			}
		}
		return false;
	}

	// �����ֻ��ŵ��Ƿ���ͨѶ¼��
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

	// ��ȡ�����ֻ��Ų���ͨѶ¼�еĺ���
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

	// ����������ѡ�е��ֻ���
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

	// ͨѶ¼������ƴ������
	public class Mycomparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			ContactInfo c1 = (ContactInfo) o1;
			ContactInfo c2 = (ContactInfo) o2;
			Comparator<Object> cmp = Collator.getInstance(java.util.Locale.CHINA);
			return cmp.compare(c1.contactName, c2.contactName);
		}

	}

	// �Ƿ�Ϊ�ֻ����� �е�ͨѶ¼��ʽΪ135-1568-1234
	public static boolean IsUserNumber(String num) {
		boolean re = false;
		if (num.length() >= 11) {
			if (num.startsWith("1")) {
				re = true;
			}

		}
		return re;
	}

	// ��ԭ11λ�ֻ��� ����ȥ����-��
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

	// ��ͨѶ¼��������ϵ�˵ķ���
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

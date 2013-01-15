package com.money.manager.ex.core;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.money.manager.ex.MoneyManagerApplication;
import com.money.manager.ex.R;
import com.money.manager.ex.database.QueryBillDeposits;

public class RepeatingTransactionAdapter extends CursorAdapter {
	private LayoutInflater inflater;
	private MoneyManagerApplication application;

	@SuppressWarnings("deprecation")
	public RepeatingTransactionAdapter(Context context, Cursor c) {
		super(context, c);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		application = (MoneyManagerApplication) context.getApplicationContext();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// take a pointer of object UI
		ImageView imgClock = (ImageView) view.findViewById(R.id.imageViewClock);
		TextView txtDate = (TextView) view.findViewById(R.id.textViewDate);
		TextView txtRepeat = (TextView) view.findViewById(R.id.textViewRepeat);
		TextView txtNextDueDate = (TextView)view.findViewById(R.id.textViewNextDueDate);
		TextView txtAmount = (TextView) view.findViewById(R.id.textViewAmount);
		TextView txtAccountName = (TextView) view.findViewById(R.id.textViewAccountName);
		TextView txtPayee = (TextView) view.findViewById(R.id.textViewPayee);
		LinearLayout linearToAccount = (LinearLayout) view.findViewById(R.id.linearLayoutToAccount);
		TextView txtToAccountName = (TextView) view.findViewById(R.id.textViewToAccountName);
		TextView txtCategorySub = (TextView) view.findViewById(R.id.textViewCategorySub);
		TextView txtNotes = (TextView) view.findViewById(R.id.textViewNotes);
		ImageView imgFollowUp = (ImageView)view.findViewById(R.id.imageViewFollowUp);
		// account name
		txtAccountName.setText(cursor.getString(cursor.getColumnIndex(QueryBillDeposits.ACCOUNTNAME)));
		// write data
		txtDate.setText(cursor.getString(cursor.getColumnIndex(QueryBillDeposits.USERNEXTOCCURRENCEDATE)));
		// take daysleft
		int daysLeft = cursor.getInt(cursor.getColumnIndex(QueryBillDeposits.DAYSLEFT));
		if (daysLeft == 0) {
			txtNextDueDate.setText(R.string.inactive);
		} else {
			txtNextDueDate.setText(Integer.toString(Math.abs(daysLeft)) + " " + context.getString(daysLeft > 0 ? R.string.days_remaining : R.string.days_overdue));
			imgClock.setVisibility(daysLeft < 0 ? View.VISIBLE : View.INVISIBLE);
		}
		// show follow up icon
		if ("F".equalsIgnoreCase(cursor.getString(cursor.getColumnIndex(QueryBillDeposits.STATUS)))) {
			imgFollowUp.setVisibility(View.VISIBLE);
		} else {
			imgFollowUp.setVisibility(View.GONE);
		}
		txtRepeat.setText(application.getRepeatAsString(cursor.getInt(cursor.getColumnIndex(QueryBillDeposits.REPEATS))));
		// take transaction amount
		float amount = cursor.getFloat(cursor.getColumnIndex(QueryBillDeposits.AMOUNT));
		// manage transfer and change amount sign
		if ((cursor.getString(cursor.getColumnIndex(QueryBillDeposits.TRANSCODE)) != null)
				&& (cursor.getString(cursor.getColumnIndex(QueryBillDeposits.TRANSCODE)).equals("Transfer"))) {
			if (cursor.getInt(cursor.getColumnIndex(QueryBillDeposits.ACCOUNTID)) != cursor.getInt(cursor.getColumnIndex(QueryBillDeposits.TOACCOUNTID))) {
				amount = -(amount); // -total
			} else if (cursor.getInt(cursor.getColumnIndex(QueryBillDeposits.TOACCOUNTID)) == cursor.getInt(cursor
					.getColumnIndex(QueryBillDeposits.TOACCOUNTID))) {
				amount = cursor.getFloat(cursor.getColumnIndex(QueryBillDeposits.TOTRANSAMOUNT)); // to account = account
			}
		}
		txtAmount.setText(application.getCurrencyFormatted(cursor.getInt(cursor.getColumnIndex(QueryBillDeposits.CURRENCYID)), amount));
		// check amount sign
		txtAmount.setTextColor(context.getResources().getColor( amount > 0 ? R.color.holo_green_light : R.color.holo_red_light ));
		// compose payee description
		String payee = cursor.getString(cursor.getColumnIndex(QueryBillDeposits.PAYEENAME));
		// write payee
		if ((!TextUtils.isEmpty(payee))) {
			txtPayee.setText(payee);
			txtPayee.setVisibility(View.VISIBLE);
		} else {
			txtPayee.setVisibility(View.GONE);
		}
		// write ToAccountName
		if ((!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(QueryBillDeposits.TOACCOUNTNAME))))) {
			txtToAccountName.setText(cursor.getString(cursor.getColumnIndex(QueryBillDeposits.TOACCOUNTNAME)));
			linearToAccount.setVisibility(View.VISIBLE);
		} else {
			linearToAccount.setVisibility(View.GONE);
		}
		// compose category description
		String categorySub = cursor.getString(cursor.getColumnIndex(QueryBillDeposits.CATEGNAME));
		// add if not null subcategory
		if (!(TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(QueryBillDeposits.SUBCATEGNAME))))) {
			categorySub += " : <i>" + cursor.getString(cursor.getColumnIndex(QueryBillDeposits.SUBCATEGNAME)) + "</i>";
		}
		// write category / subcategory format html
		if (TextUtils.isEmpty(categorySub) == false) {
			txtCategorySub.setText(Html.fromHtml(categorySub));
		} else {
			txtCategorySub.setText("");
		}
		// notes
		String notes = cursor.getString(cursor.getColumnIndex(QueryBillDeposits.NOTES));
		if (!TextUtils.isEmpty(notes)) {
			txtNotes.setText(Html.fromHtml("<small>" + notes + "</small>"));
			txtNotes.setVisibility(View.VISIBLE);
		} else {
			txtNotes.setVisibility(View.GONE);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.item_bill_deposits, parent, false);
	}
}
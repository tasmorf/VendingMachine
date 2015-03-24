package com.vending.android.controller.activity;

import com.vending.android.R;
import com.vending.android.model.VendingMachine;
import com.vending.android.model.exception.InsufficientStockException;
import com.vending.android.view.Toaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static com.vending.android.module.model.VendingMachineModule.vendingMachine;
import static com.vending.android.module.view.ToasterModule.toaster;

/**
 * Handles purchasing
 * There's was no reason to separate this between a View and a Controller, since this is quite
 * simple. If more functionality was added though, it would be worth splitting it up.
 */
public class PurchaseActivity extends Activity implements View.OnClickListener {

    private final VendingMachine mVendingMachine;
    private final Toaster mToaster;

    private TextView mAmountInsertedText;

    private int mCurrentAmountInPennies;
    private int mItemCost;

    public static Intent getPurchaseIntent(Context context) {
        return new Intent(context, PurchaseActivity.class);
    }
    
    public PurchaseActivity() {
        this(vendingMachine(), toaster());
    }

    public PurchaseActivity(VendingMachine vendingMachine, Toaster toaster) {
        mVendingMachine = vendingMachine;
        mToaster = toaster;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        findViewById(R.id.ok_button).setOnClickListener(this);
        findViewById(R.id.one_penny_button).setOnClickListener(this);
        findViewById(R.id.two_penny_button).setOnClickListener(this);
        findViewById(R.id.five_penny_button).setOnClickListener(this);
        findViewById(R.id.ten_penny_button).setOnClickListener(this);
        findViewById(R.id.twenty_penny_button).setOnClickListener(this);
        findViewById(R.id.fifty_penny_button).setOnClickListener(this);
        findViewById(R.id.one_pound_button).setOnClickListener(this);
        findViewById(R.id.two_pounds_button).setOnClickListener(this);
        mItemCost = getResources().getInteger(R.integer.item_cost_in_pennies);
        ((TextView)findViewById(R.id.purchase_prompt)).setText(getString(R.string.purchase_instructions,
                mItemCost/100f));
        mAmountInsertedText = (TextView)findViewById(R.id.amount_inserted_text);
        mAmountInsertedText.setText(getString(R.string.amount_inserted, 0f));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.one_penny_button:
                addPennies(1);
                break;
            case R.id.two_penny_button:
                addPennies(2);
                break;
            case R.id.five_penny_button:
                addPennies(5);
                break;
            case R.id.ten_penny_button:
                addPennies(10);
                break;
            case R.id.twenty_penny_button:
                addPennies(20);
                break;
            case R.id.fifty_penny_button:
                addPennies(50);
                break;
            case R.id.one_pound_button:
                addPennies(100);
                break;
            case R.id.two_pounds_button:
                addPennies(200);
                break;
            case R.id.ok_button:
                purchaseItem();
                break;
        }
    }

    private void purchaseItem() {
        try {
            int resultingChange = mVendingMachine.dispenseItem(mCurrentAmountInPennies);
            if(resultingChange > 0) {
                mToaster.displayToast(getString(R.string.purchase_success,
                        resultingChange / 100f));
                finish();
            } else {
                mToaster.displayToast(R.string.insufficient_funds_failure);
            }
        } catch (InsufficientStockException e) {
            mToaster.displayToast(R.string.insufficient_stock_failure);
        }

    }

    private void addPennies(int pennies) {
        mCurrentAmountInPennies +=pennies;
        mAmountInsertedText.setText(
                getString(R.string.amount_inserted, mCurrentAmountInPennies / 100f));
    }
}

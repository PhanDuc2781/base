package com.example.base_project.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.base_project.R;


public class NameDialog extends Dialog {
    private TextView cancelView;
    private View dividerView;
    private TextView errorView;
    private boolean isOK;
    private Listener listener;
    private String name;
    private EditText nameView;
    private TextView okView;
    private View.OnClickListener onCancelClickListener = new View.OnClickListener() {

        public void onClick(View view) {
            if (NameDialog.this.isShowing()) {
                NameDialog.this.dismiss();
            }
        }
    };
    private View.OnClickListener onOkClickListener = new View.OnClickListener() {

        public void onClick(View view) {
            if (NameDialog.this.isOK) {
                String trim = NameDialog.this.nameView.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    NameDialog.this.dividerView.setBackgroundColor(NameDialog.this.getContext().getResources().getColor(R.color.background_floating_material_light));
                    NameDialog.this.errorView.setText(R.string.app_name);
                    NameDialog.this.errorView.setVisibility(View.VISIBLE);
                    NameDialog.this.isOK = false;
                    return;
                }
                if (NameDialog.this.listener != null) {
                    NameDialog.this.listener.onOK(trim);
                }
                if (NameDialog.this.isShowing()) {
                    NameDialog.this.dismiss();
                }
            }
        }
    };
    private TextWatcher textWatcher = new TextWatcher() {

        public void afterTextChanged(Editable editable) {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            NameDialog.this.checkName(charSequence.toString().trim());
        }
    };

    public interface Listener {
        void onOK(String str);
    }

    public NameDialog(Context context, String str) {
        super(context);
        this.name = str;
        init();
    }

    private void init() {
        setContentView(R.layout.name_dialog);
        setCancelable(false);

        EditText editText = (EditText) findViewById(R.id.name_view);
        this.nameView = editText;
        editText.addTextChangedListener(this.textWatcher);
        this.dividerView = findViewById(R.id.divider_view);
        this.errorView = (TextView) findViewById(R.id.error_view);
        TextView textView = (TextView) findViewById(R.id.cancel_view);
        this.cancelView = textView;
        textView.setOnClickListener(this.onCancelClickListener);
        TextView textView2 = (TextView) findViewById(R.id.ok_view);
        this.okView = textView2;
        textView2.setOnClickListener(this.onOkClickListener);
        this.nameView.setText(this.name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    }

    private void checkName(String str) {
        if (TextUtils.isEmpty(str)) {
            this.dividerView.setBackgroundColor(getContext().getResources().getColor(R.color.red_maker));
            this.errorView.setText(R.string.name_is_empty);
            this.errorView.setVisibility(View.VISIBLE);
            this.isOK = false;
            return;
        }
        this.dividerView.setBackgroundColor(getContext().getResources().getColor(R.color.common_dialog_divider_color));
        this.errorView.setVisibility(View.GONE);
        this.isOK = true;
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }
}

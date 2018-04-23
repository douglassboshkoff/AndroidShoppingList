package com.ait.dboshko1.shoppinglist;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ait.dboshko1.shoppinglist.data.Item;

import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;

public class ItemEditAndCreateDialog extends DialogFragment {

    public interface ItemHandler {
        void onNewItemCreated(Item newItem);
        void onItemEdited(Item editItem);
    }

    private ItemHandler itemHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ItemHandler) {
            itemHandler = (ItemHandler) context;
        } else {
            throw new RuntimeException(
                    "The Activity does not implement the TodoHandler interface."
            );
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(getArguments() != null && getArguments().containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
            return buildEditDialog(((Item) getArguments().getSerializable(MainActivity.KEY_ITEM_TO_EDIT)));
        } else {
            return buildCreateDialog();
        }
    }

    private Dialog buildCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View createDialog = getActivity().getLayoutInflater().inflate(R.layout.item_edit_and_delete_dialog, null);
        setUpSpinner(createDialog, "");

        builder.setTitle(R.string.new_item)
                .setView(createDialog)
                .setPositiveButton(R.string.save_item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    public void onResume()
    {
        super.onResume();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validFormFields(d)) {
                        if (getArguments() != null && getArguments().containsKey(MainActivity.KEY_ITEM_TO_EDIT)) {
                            Item editItem = ((Item) getArguments().getSerializable(MainActivity.KEY_ITEM_TO_EDIT));

                            updateItemFromFields(d, editItem);
                            itemHandler.onItemEdited(editItem);
                        } else {
                            itemHandler.onNewItemCreated(extractItemFromDialog(d));
                        }
                        d.dismiss();
                    }
                }
            });
        }
    }

    private boolean validFormFields(AlertDialog d) {
        return validateEditText((EditText) d.findViewById(R.id.tvItemName)) &
                validateEditText((EditText) d.findViewById(R.id.tvItemPrice)) &
                validateSpinner((MaterialSpinner) d.findViewById(R.id.spCategory));
    }

    private boolean validateSpinner(MaterialSpinner spinner) {
        if(spinner.getSelectedItem() == null) {
            spinner.setError(getString(R.string.spErrorMsg));
            return false;
        }
        return true;
    }

    private boolean validateEditText(EditText et) {
        if(TextUtils.isEmpty(et.getText())) {
            et.setError(getString(R.string.etError));
            return false;
        } else {
            return true;
        }
    }

    private void setUpSpinner(View createDialog, String selected) {
        String[] test = Item.getCategories();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, test);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MaterialSpinner spinner = createDialog.findViewById(R.id.spCategory);
        spinner.setAdapter(arrayAdapter);

        if(!selected.equals("")) {
            spinner.setSelection(arrayAdapter.getPosition(selected) + 1);
        } else {
            spinner.setSelection(0);
        }
    }

    private Item extractItemFromDialog(AlertDialog dialog) {
        return new Item(((TextView) dialog.findViewById(R.id.tvItemName)).getText().toString(),
                ((MaterialSpinner) dialog.findViewById(R.id.spCategory)).getSelectedItem().toString(),
                ((TextView) dialog.findViewById(R.id.tvItemDescription)).getText().toString(),
                Double.parseDouble(((TextView) dialog.findViewById(R.id.tvItemPrice)).getText().toString()),
                ((CheckBox) dialog.findViewById(R.id.cbPurchased)).isChecked());
    }

    private Dialog buildEditDialog(final Item editItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View editDialog = getActivity().getLayoutInflater().inflate(R.layout.item_edit_and_delete_dialog, null);

        fillDialogFromItem(editItem, editDialog);


        builder.setTitle(R.string.edit_item)
                .setView(editDialog)
                .setPositiveButton(R.string.save_item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    private void updateItemFromFields(AlertDialog editDialog, Item editItem) {
        editItem.setItemName(((TextView) editDialog.findViewById(R.id.tvItemName)).getText().toString());
        editItem.setItemCategory(((MaterialSpinner) editDialog.findViewById(R.id.spCategory)).getSelectedItem().toString());
        editItem.setItemDescription(((TextView) editDialog.findViewById(R.id.tvItemDescription)).getText().toString());
        editItem.setItemEstimatedPrice(Double.parseDouble(((TextView) editDialog.findViewById(R.id.tvItemPrice)).getText().toString()));
        editItem.setItemBought(((CheckBox) editDialog.findViewById(R.id.cbPurchased)).isChecked());
    }

    private void fillDialogFromItem(Item editItem, View editDialog) {
        fillFieldsFromItem(editDialog, editItem);
        setUpSpinner(editDialog, editItem.getItemCategory());
        ((CheckBox) editDialog.findViewById(R.id.cbPurchased)).setChecked(editItem.isItemBought());
    }

    private void fillFieldsFromItem(View editDialog, Item item) {
        fillFieldFromID(editDialog, R.id.tvItemName, item.getItemName());
        fillFieldFromID(editDialog, R.id.tvItemDescription, item.getItemDescription());
        fillFieldFromID(editDialog, R.id.tvItemPrice, String.format(Locale.US,
                "%.2f",
                item.getItemEstimatedPrice()));
    }

    private void fillFieldFromID(View editDialog, int resourceId, String fieldText) {
        EditText editText = editDialog.findViewById(resourceId);
        editText.setText(fieldText);
        editText.setSelection(editText.getText().length());
    }
}

package io.github.fvarrui.reviser.ui.utils;

import java.io.File;

import javafx.scene.control.ListCell;

public class FileListCell extends ListCell<File> {

	@Override
	protected void updateItem(File item, boolean empty) {
		super.updateItem(item, empty);
		setText(item != null ? item.getName() : "");
	}
	
}

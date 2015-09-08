package edu.unc.flashlight.client.ui.widget;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

import edu.unc.flashlight.client.Flashlight;
import edu.unc.flashlight.client.FlashlightConstants;
import edu.unc.flashlight.client.command.ServerOp;
import edu.unc.flashlight.shared.model.User;

public class UserTable extends Composite {

	CellTable<User> cellTable;
	private Label emptyTableWidget;
	final MultiSelectionModel<User> selectionModel;
	private FlashlightConstants constants = FlashlightConstants.INSTANCE;

	public UserTable() {
		VerticalPanel panel = new VerticalPanel();
		initWidget(panel);
		cellTable = new CellTable<User>();
		panel.add(cellTable);
		emptyTableWidget = new Label(constants.datasets_noResults());
		emptyTableWidget.setStylePrimaryName("emptyTableLabel");
		selectionModel = new MultiSelectionModel<User>(keyProvider);
		cellTable.setSelectionModel(selectionModel,DefaultSelectionEventManager.<User>createCheckboxManager());
		initTableColumns();
		updateTable();
	}

	private ProvidesKey<User> keyProvider = new ProvidesKey<User>() {
		public Object getKey(User item) {
			return (item == null) ? null : item.getId();
		}
	};

	public void updateTable() {
		new ServerOp<List<User>>() {
			public void onSuccess(List<User> result) {
				cellTable.setRowData(result);
				cellTable.setEmptyTableWidget(emptyTableWidget);
			}
			public void begin() {
				Flashlight.userService.getPublicUsers(this);
			}
		}.begin();
	}

	private void initTableColumns() {
	    Column<User, Boolean> checkColumn = new Column<User, Boolean>(new CheckboxCell(true, false)) {
	          @Override
	          public Boolean getValue(User object) {
	            return selectionModel.isSelected(object);
	          }
	        };
	       
		Column<User, String> usernameColumn = new Column<User, String>(new TextCell()) {
			public String getValue(User object) {
				return String.valueOf(object.getUsername());
			}
		};
		
		Column<User, String> purificationMethodColumn = new Column<User, String>(new TextCell()) {
			public String getValue(User object) {
				return String.valueOf(object.getPurificationMethod());
			}
		};
		
		Column<User, String> labNameColumn = new Column<User, String>(new TextCell()) {
			public String getValue(User object) {
				return String.valueOf(object.getLabName());
			}
		};
		
		Column<User, String> pubmedColumn = new Column<User, String>(new ClickableImageCell()) {
			public String getValue(User object) {
				return "images/pubmed2-01_round.png";
			}
		};
		
		pubmedColumn.setFieldUpdater(new FieldUpdater<User, String>() {
			public void update(int index, final User object, String value) {
				com.google.gwt.user.client.Window.open("http://www.ncbi.nlm.nih.gov/pubmed/"+object.getPubmedId(), "_blank", "");				
			}
		});
		
		Column<User, String> numExpColumn = new Column<User, String>(new TextCell()) {
			public String getValue(User object) {
				return String.valueOf(object.getNumExp());
			}
		};
		
		Column<User, String> numCtrlColumn = new Column<User, String>(new TextCell()) {
			public String getValue(User object) {
				return String.valueOf(object.getNumCtrl());
			}
		};
		
		Column<User, String> numInteractionColumn = new Column<User, String>(new TextCell()) {
			public String getValue(User object) {
				return String.valueOf(object.getNumInteractions());
			}
		};
		
		cellTable.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
		cellTable.addColumn(usernameColumn, constants.datasetsCol_dataset());
		cellTable.addColumn(purificationMethodColumn, constants.datasetsCol_APMethod());
		cellTable.addColumn(labNameColumn, constants.datasetsCol_lab());
		cellTable.addColumn(pubmedColumn, constants.datasetsCol_pubmed());
		cellTable.addColumn(numExpColumn, constants.datasetsCol_numExps());
		cellTable.addColumn(numCtrlColumn, constants.datasetsCol_numCtrls());
		cellTable.addColumn(numInteractionColumn, constants.datasetsCol_numInteractions());
	}
	
	public Set<Long> getSelectedIds() {
		Set<Long> selectedIds = new HashSet<Long>();
		for (User u : selectionModel.getSelectedSet()) {
			selectedIds.add(u.getId());
		}
		return selectedIds;
	}
}


package ph.txtdis.service;

import java.time.LocalDate;

import ph.txtdis.dto.Inventory;

public interface InventoryService extends Spreadsheet<Inventory> {

	LocalDate getDate();

	Inventory getInventory(Long itemId) throws Exception;

	void setDate(LocalDate date);
}
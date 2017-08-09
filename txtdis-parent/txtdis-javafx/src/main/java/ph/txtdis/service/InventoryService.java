package ph.txtdis.service;

import ph.txtdis.dto.Inventory;

import java.time.LocalDate;

public interface InventoryService
	extends Spreadsheet<Inventory> {

	LocalDate getDate();

	void setDate(LocalDate date);

	Inventory getInventory(Long itemId) throws Exception;
}
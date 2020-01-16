package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	
	private SellerDao sd = DaoFactory.createSellerDao();
	
	public List<Seller> findAll() {
		
		return sd.findAll();
		
	}
	
	public void saveOrUpdate(Seller seller) {
		if (seller.getId() == null) {
			sd.insert(seller);
		}
		else {
			sd.update(seller);
		}
	}
	
	public void delete(Seller seller) {
		if (seller.getId() != null) {
			sd.deleteById(seller.getId());
		}
	}

}

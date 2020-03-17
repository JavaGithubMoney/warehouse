package com.warehouse.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 仓库管理的controller
 * 2018.5.2
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.warehouse.bean.Log;
import com.warehouse.bean.Warehouse;
import com.warehouse.common.WMessage;
import com.warehouse.common.WResponse;
import com.warehouse.service.LogService;
import com.warehouse.service.WarehouseService;
import com.warehouse.utils.TimeUtil;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {

	@Autowired
	private WarehouseService warehouseService;
	
	@Autowired
	private LogService logService;
	
	@ResponseBody
	@RequestMapping(value = "/addWarehouse", method = RequestMethod.POST)
	public WResponse addWarehouse(Warehouse warehouse,@RequestParam("contract_date") String contractdate,HttpSession session) throws Exception {
		WResponse response=new WResponse();
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date date =sdf.parse(contractdate);
		warehouse.setContractDate(date);
		warehouse.setOperator((String)session.getAttribute("username"));
		if(warehouseService.checkWarehouseExists(warehouse.getNum())) {
			response.setMessage(WMessage.MSG_WAREHOUSE_EXISTS);
		}else {
			Integer num=warehouseService.insertWarehouse(warehouse);
			if(num>0) {
				response.setMessage(WMessage.MSG_SUCCESS);
			}else {
				response.setMessage(WMessage.MSG_FAIL);
			}
		}
		
		Log log=logService.createLog((String)session.getAttribute("username"),
									WMessage.MSG_OPREATION_ADD,
									"用户： "+session.getAttribute("username")
									+" 等级： "+session.getAttribute("level")
									+" 于 "+TimeUtil.getNowerTime()
									+" 添加仓库信息： "
									+" 仓库编号： "+warehouse.getNum()
									+" 仓库名： "+warehouse.getName()
									+" 负责人： "+warehouse.getResponser()
									+" 负责人电话： "+warehouse.getResponserPhone()
									+" 合同签订日期： "+warehouse.getContractDate()
									+" 所属公司： "+warehouse.getCompany()
									+" 备注： "+warehouse.getRemark()
									+" 结果： "+ response.getMessage());
		logService.insertLog(log);
		
		return response;
	}
	
	@RequestMapping(value = "/manageWarehouse", method = RequestMethod.GET)
	public String manageWarehouse(HttpSession session,Model model) throws Exception {
		
		if(session.getAttribute("username")==null) {
			return "error";
		}
		
		List<Warehouse> warehouselist=warehouseService.selectAllWarehouse();
		model.addAttribute("warehouselist",warehouselist);
		return "manageWarehouse";
	}
	
	@ResponseBody
	@RequestMapping(value = "/updateWarehouse", method = RequestMethod.POST)
	public WResponse updateWarehouse(Warehouse warehouse,HttpSession session) throws Exception {
		
		WResponse response=new WResponse();
		warehouse.setOperator((String)session.getAttribute("username"));
		
		Integer num=warehouseService.updateWarehouse(warehouse);
		if(num>0) {
			response.setMessage(WMessage.MSG_SUCCESS);
		}else {
			response.setMessage(WMessage.MSG_FAIL);
		}
		
		Log log=logService.createLog((String)session.getAttribute("username"),
				WMessage.MSG_OPREATION_UPDATE,
				"用户： "+session.getAttribute("username")
				+" 等级： "+session.getAttribute("level")
				+" 于 "+TimeUtil.getNowerTime()
				+" 更新仓库信息为： "
				+" 仓库编号： "+warehouse.getNum()
				+" 仓库名： "+warehouse.getName()
				+" 负责人： "+warehouse.getResponser()
				+" 负责人电话： "+warehouse.getResponserPhone()
				+" 所属公司： "+warehouse.getCompany()
				+" 备注： "+warehouse.getRemark()
				+" 结果： "+ response.getMessage());
		logService.insertLog(log);
		
		return response;
	}
}

/*
package com.rengu.project.aluminum.service.impl;

import com.fxtcn.platform.dao.TaskDao;
import com.fxtcn.platform.entity.TaskAPIData;
import com.fxtcn.platform.entity.TaskData;
import com.fxtcn.platform.service.CustomTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomTaskServiceImpl  implements CustomTaskService{
	@Autowired
	private TaskDao taskDao;
	@Override
	public List<TaskData> taskListPage(String userId) {
		return taskDao.queryByUserIdListPage(userId);
	}
	
	@Override
	public List<TaskAPIData> queryByUserIdPage(String userId) {

		return taskDao.queryByUserIdPage(userId);
	}

}
*/

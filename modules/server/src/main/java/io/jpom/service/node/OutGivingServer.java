package io.jpom.service.node;

import io.jpom.model.data.OutGivingModel;
import io.jpom.model.data.OutGivingNodeProject;
import io.jpom.service.h2db.BaseWorkspaceService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分发管理
 *
 * @author jiangzeyin
 * @date 2019/4/21
 */
@Service
public class OutGivingServer extends BaseWorkspaceService<OutGivingModel> {

//    public OutGivingServer() {
//        super(ServerConfigBean.OUTGIVING);
//    }

	public boolean checkNode(String nodeId, HttpServletRequest request) {
		List<OutGivingModel> list = super.listByWorkspace(request);
		if (list == null || list.isEmpty()) {
			return false;
		}
		for (OutGivingModel outGivingModel : list) {
			List<OutGivingNodeProject> outGivingNodeProjectList = outGivingModel.outGivingNodeProjectList();
			if (outGivingNodeProjectList != null) {
				for (OutGivingNodeProject outGivingNodeProject : outGivingNodeProjectList) {
					if (outGivingNodeProject.getNodeId().equals(nodeId)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}

package org.zstack.storage.primary;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.core.workflow.FlowTrigger;
import org.zstack.header.core.workflow.NoRollbackFlow;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.header.storage.primary.PrimaryStorageAllocationSpec;
import org.zstack.header.storage.primary.PrimaryStorageConstant.AllocatorParams;
import org.zstack.header.storage.primary.PrimaryStorageVO;
import org.zstack.utils.DebugUtils;
import org.zstack.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class PrimaryStorageReservedCapacityAllocatorFlow extends NoRollbackFlow {
    @Autowired
    protected ErrorFacade errf;

    @Override
    public void run(FlowTrigger trigger, Map data) {
        PrimaryStorageAllocationSpec spec = (PrimaryStorageAllocationSpec) data.get(AllocatorParams.SPEC);
        List<PrimaryStorageVO> candidates = (List<PrimaryStorageVO>) data.get(AllocatorParams.CANDIDATES);
        DebugUtils.Assert(candidates != null && !candidates.isEmpty(), "PrimaryStorageReservedCapacityAllocatorFlow cannot be the first element in allocator chain");

        long reservedCapacity = SizeUtils.sizeStringToBytes(PrimaryStorageGlobalConfig.RESERVED_CAPACITY.value());
        List<PrimaryStorageVO> ret = new ArrayList<PrimaryStorageVO>(candidates.size());
        for (PrimaryStorageVO vo : candidates) {
            if (vo.getCapacity().getAvailableCapacity() - reservedCapacity > spec.getSize()) {
                ret.add(vo);
            }
        }

        if (ret.isEmpty()) {
            throw new OperationFailureException(errf.stringToOperationError(
                    String.format("after subtracting reserved capacity[%s], there is no primary storage having required size[%s bytes]",
                            PrimaryStorageGlobalConfig.RESERVED_CAPACITY.value(), spec.getSize())
            ));
        }

        data.put(AllocatorParams.CANDIDATES, ret);
        trigger.next();
    }
}


package org.zstack.header.volume;

import org.zstack.header.configuration.DiskOfferingInventory;
import org.zstack.header.configuration.PythonClassInventory;
import org.zstack.header.image.ImageInventory;
import org.zstack.header.query.*;
import org.zstack.header.search.Inventory;
import org.zstack.header.storage.primary.PrimaryStorageInventory;
import org.zstack.header.storage.snapshot.VolumeSnapshotInventory;
import org.zstack.header.vm.VmInstanceInventory;

import javax.persistence.JoinColumn;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * @inventory
 * inventory for volume
 *
 * @category volume
 *
 * @example
 *
 * {
"inventory": {
"uuid": "d4910ee8def241e7afcb55ca1ee685c9",
"name": "d1",
"primaryStorageUuid": "29ea91d6cfb544a392b24f84a43de154",
"vmInstanceUuid": "0135cb45094f4f6fb84375e13d4a1cb8",
"installPath": "/opt/zstack/nfsprimarystorage/prim-29ea91d6cfb544a392b24f84a43de154/dataVolumes/acct-36c27e8ff05c4780bf6d2fa65700f22e/vol-d4910ee8def241e7afcb55ca1ee685c9/d4910ee8def241e7afcb55ca1ee685c9.qcow2",
"type": "Data",
"hypervisorType": "KVM",
"size": 32212254720,
"deviceId": 1,
"state": "Enabled",
"status": "Ready",
"createDate": "May 2, 2014 7:55:15 PM",
"lastOpDate": "May 2, 2014 7:55:15 PM",
"backupStorageRefs": [
{
"volumeUuid": "d4910ee8def241e7afcb55ca1ee685c9",
"backupStorageUuid": "e028f12592fa40359b9af5b8946b1c53",
"installPath": "nfs:/test1/volumeSnapshots/acct-36c27e8ff05c4780bf6d2fa65700f22e/d4910ee8def241e7afcb55ca1ee685c9/d4910ee8def241e7afcb55ca1ee685c9.qcow2"
}
]
}
}
 *
 * @since 0.1.0
 */
@Inventory(mappingVOClass = VolumeVO.class)
@PythonClassInventory
@ExpandedQueries({
        @ExpandedQuery(expandedField = "vmInstance", inventoryClass = VmInstanceInventory.class,
                foreignKey = "vmInstanceUuid", expandedInventoryKey = "uuid"),
        @ExpandedQuery(expandedField = "snapshot", inventoryClass = VolumeSnapshotInventory.class,
                foreignKey = "uuid", expandedInventoryKey = "volumeUuid"),
        @ExpandedQuery(expandedField = "diskOffering", inventoryClass = DiskOfferingInventory.class,
                foreignKey = "diskOfferingUuid", expandedInventoryKey = "uuid"),
        @ExpandedQuery(expandedField = "primaryStorage", inventoryClass = PrimaryStorageInventory.class,
                foreignKey = "primaryStorageUuid", expandedInventoryKey = "uuid"),
        @ExpandedQuery(expandedField = "image", inventoryClass = ImageInventory.class,
                foreignKey = "rootImageUuid", expandedInventoryKey = "uuid"),
})
public class VolumeInventory implements Serializable{
    /**
     * @desc
     * volume uuid
     */
    private String uuid;
    /**
     * @desc
     * max length of 255 characters
     */
    private String name;
    /**
     * @desc
     * max length of 2048 characters
     * @nullable
     */
    private String description;
    /**
     * @desc
     * uuid of primary storage the volume is on. See :ref:`PrimaryStorageInventory`
     */
    private String primaryStorageUuid;
    /**
     * @desc
     * uuid of vm the volume is attached to. If null, the volume isn't attached
     * @nullable
     */
    private String vmInstanceUuid;

    private String diskOfferingUuid;
    /**
     * @desc
     * uuid of image from which the volume is created when type = 'Root'. Null when type = 'Data'
     * @nullable
     */
    private String rootImageUuid;
    /**
     * @desc
     * path the volume locates on primary storage. Depending on primary storage type, this field may have various meanings.
     * For example, for nfs primary storage it is filesystem path
     */
    private String installPath;
    /**
     * @desc
     * - Root: vm's root volume where operating system was installed
     * - Data: data volume that can be attached/detached to/from vm
     *
     * @choices
     * - Root
     * - Data
     */
    private String type;

    private String format;
    /**
     * @desc volume size in bytes
     */
    private Long size;
    /**
     * @desc the order volume attaches to vm. For root volume, deviceId is always zero. For data volume, deviceId could be used
     * for detecting disk label in operating system. For example, volume having deviceId = 1 may be represented as hdb/sdb/vdb in Linux.
     */
    private Integer deviceId;
    /**
     * @desc
     * - Enabled: the volume is ok for operations
     * - Disabled: the volume can not be attached to vm
     *
     * .. note:: state is only meaningful for data volume. Root volume always has state Enabled that can not be changed
     */
    private String state;
    /**
     * @desc
     * - Creating: volume is being created from other resource, for example, volume snapshot.
     * - Ready: the volume is ok for operations
     * - NotInstantiated: volume is created in database but has not initialized on any primary storage. The volume will be initialized
     *   at first time it attaches to vm and change to status Ready then.
     *
     * .. note:: status is only meaningful for data volume. Root volume always has status Ready that can not be changed
     */
    private String status;
    /**
     * @desc the time this resource gets created
     */
    private Timestamp createDate;
    /**
     * @desc last time this resource gets operated
     */
    private Timestamp lastOpDate;

    public VolumeInventory() {
    }

    public VolumeInventory(VolumeInventory other) {
        this.uuid = other.uuid;
        this.name = other.name;
        this.description = other.description;
        this.primaryStorageUuid = other.primaryStorageUuid;
        this.vmInstanceUuid = other.vmInstanceUuid;
        this.diskOfferingUuid = other.diskOfferingUuid;
        this.rootImageUuid = other.rootImageUuid;
        this.installPath = other.installPath;
        this.type = other.type;
        this.format = other.format;
        this.size = other.size;
        this.deviceId = other.deviceId;
        this.state = other.state;
        this.status = other.status;
        this.createDate = other.createDate;
        this.lastOpDate = other.lastOpDate;
    }


    public static VolumeInventory valueOf(VolumeVO vo) {
    	VolumeInventory inv = new VolumeInventory();
    	inv.setRootImageUuid(vo.getRootImageUuid());
    	inv.setCreateDate(vo.getCreateDate());
    	inv.setDescription(vo.getDescription());
    	inv.setInstallPath(vo.getInstallPath());
    	inv.setName(vo.getName());
    	inv.setPrimaryStorageUuid(vo.getPrimaryStorageUuid());
    	inv.setSize(vo.getSize());
    	inv.setState(vo.getState().toString());
    	inv.setUuid(vo.getUuid());
    	inv.setVmInstanceUuid(vo.getVmInstanceUuid());
    	inv.setType(vo.getType().toString());
        inv.setDiskOfferingUuid(vo.getDiskOfferingUuid());
    	inv.setCreateDate(vo.getCreateDate());
    	inv.setLastOpDate(vo.getLastOpDate());
    	inv.setDeviceId(vo.getDeviceId());
        inv.setStatus(vo.getStatus().toString());
        inv.setFormat(vo.getFormat());
    	return inv;
    }
    
    public static List<VolumeInventory> valueOf(Collection<VolumeVO> vos) {
        List<VolumeInventory> invs = new ArrayList<VolumeInventory>(vos.size());
        for (VolumeVO vo : vos) {
            invs.add(VolumeInventory.valueOf(vo));
        }
        return invs;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDiskOfferingUuid() {
        return diskOfferingUuid;
    }

    public void setDiskOfferingUuid(String diskOfferingUuid) {
        this.diskOfferingUuid = diskOfferingUuid;
    }

    public String getUuid() {
    	return uuid;
    }
	public void setUuid(String uuid) {
    	this.uuid = uuid;
    }
	public String getName() {
    	return name;
    }
	public void setName(String name) {
    	this.name = name;
    }
	public String getDescription() {
    	return description;
    }
	public void setDescription(String description) {
    	this.description = description;
    }
	public String getPrimaryStorageUuid() {
    	return primaryStorageUuid;
    }
	public void setPrimaryStorageUuid(String primaryStorageUuid) {
    	this.primaryStorageUuid = primaryStorageUuid;
    }
	public String getVmInstanceUuid() {
    	return vmInstanceUuid;
    }
	public void setVmInstanceUuid(String vmInstanceUuid) {
    	this.vmInstanceUuid = vmInstanceUuid;
    }
	public String getInstallPath() {
    	return installPath;
    }
	public void setInstallPath(String installPath) {
    	this.installPath = installPath;
    }
	public String getType() {
    	return type;
    }
	public void setType(String volumeType) {
    	this.type = volumeType;
    }
	public long getSize() {
    	return size;
    }
	public void setSize(long size) {
    	this.size = size;
    }
	
	public boolean isAttached() {
    	return this.vmInstanceUuid != null;
    }

	public String getState() {
    	return state;
    }
	public void setState(String state) {
    	this.state = state;
    }

    public String getRootImageUuid() {
        return rootImageUuid;
    }

    public void setRootImageUuid(String rootImageUuid) {
        this.rootImageUuid = rootImageUuid;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}

package clearcl;

import java.util.ArrayList;

import clearcl.abs.ClearCLBase;
import clearcl.backend.ClearCLBackendInterface;
import clearcl.selector.BadDeviceSelector;
import clearcl.selector.DeviceSelector;
import clearcl.selector.DeviceTypeSelector;
import clearcl.selector.FastestDeviceSelector;
import clearcl.selector.GlobalMemorySelector;

/**
 * ClearCL is the starting point for creating ClearCL objects for OpenCL.
 * Instances of this class are constructed by providing a backend.
 * 
 * @author royer
 */
public class ClearCL extends ClearCLBase
{

  /**
   * Creates a ClearCL instance for a given ClearCL backend.
   * 
   * @param pClearCLBackendInterface
   *          backend
   */
  public ClearCL(ClearCLBackendInterface pClearCLBackendInterface)
  {
    super(pClearCLBackendInterface, null);
  }

  /**
   * Returns the number of available platforms
   * 
   * @return number of platforms
   */
  public int getNumberOfPlatforms()
  {
    return getBackend().getNumberOfPlatforms();
  }

  /**
   * Returns a platform object for a given platform index.
   * 
   * @param pPlatformIndex
   *          OpenCL platform index
   * @return
   */
  public ClearCLPlatform getPlatform(int pPlatformIndex)
  {
    ClearCLPeerPointer lPlatformIdPointer = getBackend().getPlatformPeerPointer(pPlatformIndex);
    return new ClearCLPlatform(this, lPlatformIdPointer);
  }

  /**
   * Returns the fastest GPU device available. This method does its best to
   * avoid crappy Intel integrated cards (except Iris which is not too shabby).
   * 
   * @return fastest GPU device
   */
  public ClearCLDevice getFastestGPUDevice()
  {
    ClearCLDevice lClearClDevice = getBestDevice(DeviceTypeSelector.GPU,
                                                 BadDeviceSelector.NotSlowIntegratedIntel,
                                                 FastestDeviceSelector.Fastest);
    return lClearClDevice;
  }

  /**
   * Returns the GPU device with most global memory available - hence 'largest'.
   * 
   * @return fastest GPU device
   */
  public ClearCLDevice getLargestGPUDevice()
  {
    ClearCLDevice lClearClDevice = getBestDevice(DeviceTypeSelector.GPU,
                                                 BadDeviceSelector.NotIntegratedIntel,
                                                 GlobalMemorySelector.MAX);
    return lClearClDevice;
  }
  
  /**
   * Returns the best (right now fastest) CPU device.
   * 
   * @return fastest CPU device
   */
  public ClearCLDevice getBestCPUDevice()
  {
    ClearCLDevice lClearClDevice = getBestDevice(DeviceTypeSelector.CPU,
                                                 FastestDeviceSelector.Fastest);
    return lClearClDevice;
  }

  /**
   * Picks the one (first) of the best devices obtained by using the given
   * selectors. Important: The selectors are applied in the order provided. For
   * example, you want first to select GPU devices and then pick the one with
   * the most global memory, not the other way around. The selection algorithm
   * works as follows: for each selector, the list of devices is filtered
   * accordingly, as soon as there is only one device left in the current list
   * of devices, the filtering stops - even if there are more selectors to
   * apply. Some selectors pick a single device from the list (max global mem
   * size), others pick devices of a certain type (e.g. GPU or CPU).
   * 
   * Example:
   * 
   * <pre>
   * getBestDevice(DeviceTypeSelector.GPU,
   *               BadDeviceSelector.NotIntegratedIntel,
   *               GlobalMemorySelector.MAX)
   * </pre>
   * 
   * @param pDeviceSelectors
   *          list of selectors
   * @return best device
   */
  public ClearCLDevice getBestDevice(DeviceSelector... pDeviceSelectors)
  {
    ArrayList<ClearCLDevice> lBestDevices = getBestDevices(pDeviceSelectors);
    if (lBestDevices.size() > 0)
      return lBestDevices.get(0);
    else
      return null;
  }

  /**
   * Returns a list of 'best devices' obtained by taking the list of all devices
   * for all platforms and applying the selectors as filters. Important: The
   * selectors are applied in the order provided. For example, you want first to
   * select GPU devices and then pick the one with the most global memory, not
   * the other way around. The selection algorithm works as follows: for each
   * selector, the list of devices is filtered accordingly, as soon as there is
   * only one device left in the current list of devices, the filtering stops -
   * even if there are more selectors to apply. Some selectors pick a single
   * device from the list (max global mem size), others pick devices of a
   * certain type (e.g. GPU or CPU).
   * 
   * @param pDeviceSelectors
   * @return
   */
  public ArrayList<ClearCLDevice> getBestDevices(DeviceSelector... pDeviceSelectors)
  {

    ArrayList<ClearCLDevice> lSelectedDeviceList = getAllDevices();

    for (DeviceSelector lDeviceSelector : pDeviceSelectors)
    {
      if (lSelectedDeviceList.size() == 1)
        break;

      // System.out.println(lDeviceSelector.getClass().getName());
      // System.out.println(lSelectedDeviceList);
      lDeviceSelector.init(lSelectedDeviceList);

      ArrayList<ClearCLDevice> lTempSelectedList = new ArrayList<>();
      for (ClearCLDevice lDevice : lSelectedDeviceList)
      {
        boolean lSelected = lDeviceSelector.selected(lDevice);
        if (lSelected)
          lTempSelectedList.add(lDevice);
      }

      lSelectedDeviceList.clear();
      lSelectedDeviceList.addAll(lTempSelectedList);

    }

    return lSelectedDeviceList;
  }

  /**
   * Returns all devices for all platforms.
   * 
   * @return all devices list
   */
  public ArrayList<ClearCLDevice> getAllDevices()
  {
    ArrayList<ClearCLDevice> lSelectedDeviceList = new ArrayList<>();

    int lNumberOfPlatforms = getNumberOfPlatforms();
    for (int i = 0; i < lNumberOfPlatforms; i++)
    {
      ClearCLPlatform lPlatform = getPlatform(i);
      int lNumberOfDevices = lPlatform.getNumberOfDevices();
      for (int d = 0; d < lNumberOfDevices; d++)
      {
        ClearCLDevice lClearClDevice = lPlatform.getDevice(d);
        lSelectedDeviceList.add(lClearClDevice);
      }
    }
    return lSelectedDeviceList;
  }

  /* (non-Javadoc)
   * @see clearcl.ClearCLBase#close()
   */
  @Override
  public void close()
  {

  }

}

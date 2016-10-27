package clearcl;

import java.io.IOException;

import clearcl.abs.ClearCLBase;
import clearcl.enums.HostAccessType;
import clearcl.enums.ImageChannelDataType;
import clearcl.enums.ImageChannelOrder;
import clearcl.enums.ImageType;
import clearcl.enums.KernelAccessType;
import clearcl.enums.MemAllocMode;
import coremem.types.NativeTypeEnum;

/**
 * ClearCLContext is the ClearCL abstraction for OpenCl contexts.
 *
 * @author royer
 */
public class ClearCLContext extends ClearCLBase
{

  private final ClearCLDevice mDevice;

  private final ClearCLQueue mDefaultQueue;

  /**
   * Construction of this object is done from within a ClearClDevice.
   * 
   * @param pClearCLDevice
   *          device
   * @param pContextPointer
   *          context peer pointer
   */
  ClearCLContext(ClearCLDevice pClearCLDevice,
                 ClearCLPeerPointer pContextPointer)
  {
    super(pClearCLDevice.getBackend(), pContextPointer);
    mDevice = pClearCLDevice;

    mDefaultQueue = createQueue();
  }

  /**
   * Returns the default queue. All devices are created with a default queue.
   * 
   * @return default queue
   */
  public ClearCLQueue getDefaultQueue()
  {
    return mDefaultQueue;
  }

  /**
   * Creates a queue.
   * 
   * @return queue
   */
  public ClearCLQueue createQueue()
  {
    ClearCLPeerPointer lQueuePointer = getBackend().getQueuePeerPointer(mDevice.getPeerPointer(),
                                                                        getPeerPointer(),
                                                                        true);
    ClearCLQueue lClearCLQueue = new ClearCLQueue(this, lQueuePointer);
    return lClearCLQueue;
  }

  /**
   * Creates an OpenCL buffer with a given access policy, data type and length.
   * 
   * @param pHostAccessType
   *          host access type
   * @param pKernelAccessType
   *          kernel access type
   * @param pNativeType
   *          data type
   * @param pBufferLengthInElements
   *          length in elements
   * @return created buffer
   */
  public ClearCLBuffer createBuffer(HostAccessType pHostAccessType,
                                    KernelAccessType pKernelAccessType,
                                    NativeTypeEnum pNativeType,
                                    long pBufferLengthInElements)
  {
    return createBuffer(pHostAccessType,
                        pKernelAccessType,
                        pNativeType,
                        MemAllocMode.None,
                        pBufferLengthInElements);
  }

  /**
   * Creates an OpenCL buffer with a given access policy, data type, memory
   * allocation mode and length.
   * 
   * @param pHostAccessType
   *          host access type
   * @param pKernelAccessType
   *          kernel access type
   * @param pDataType
   *          data type
   * @param pMemAllocMode
   *          memory allocation mode
   * @param pBufferLengthInElements
   *          length in elements
   * @return
   */
  public ClearCLBuffer createBuffer(NativeTypeEnum pNativeType,
                                    MemAllocMode pMemAllocMode,
                                    long pBufferLengthInElements)
  {
    return createBuffer(HostAccessType.ReadWrite,
                        KernelAccessType.ReadWrite,
                        pNativeType,
                        pMemAllocMode,
                        pBufferLengthInElements);
  }

  /**
   * Creates an OpenCL buffer with a given data type, memory allocation mode and
   * length. The host and kernel access policy is read and write access for
   * both.
   * 
   * @param pHostAccessType
   *          host access type
   * @param pKernelAccessType
   *          kernel access type
   * @param pDataType
   *          data type
   * @param pMemAllocMode
   *          memory allocation mode
   * @param pBufferLengthInElements
   *          length in elements
   * @return
   */
  public ClearCLBuffer createBuffer(HostAccessType pHostAccessType,
                                    KernelAccessType pKernelAccessType,
                                    NativeTypeEnum pNativeType,
                                    MemAllocMode pMemAllocMode,
                                    long pBufferLengthInElements)
  {

    long lBufferSizeInBytes = pBufferLengthInElements * pNativeType.getSizeInBytes();

    ClearCLPeerPointer lBufferPointer = getBackend().getBufferPeerPointer(getPeerPointer(),
                                                                          pHostAccessType,
                                                                          pKernelAccessType,
                                                                          lBufferSizeInBytes);

    ClearCLBuffer lClearCLBuffer = new ClearCLBuffer(this,
                                                     lBufferPointer,
                                                     pHostAccessType,
                                                     pKernelAccessType,
                                                     pNativeType,
                                                     pBufferLengthInElements);
    return lClearCLBuffer;
  }

  /**
   * Creates 1D, 2D, or 3D image with a given channel order, channel data type,
   * and dimensions. The host and kernel access policy is read and write access
   * for both.
   * 
   * @param pImageType
   *          image type (1D, 2D, 3D)
   * @param pImageChannelOrder
   *          channel order
   * @param pImageChannelType
   *          channel data type
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @return 1D,2D, or 3D image
   */
  public ClearCLImage createImage(ImageType pImageType,
                                  ImageChannelOrder pImageChannelOrder,
                                  ImageChannelDataType pImageChannelType,
                                  long... pDimensions)
  {
    return createImage(HostAccessType.ReadWrite,
                       KernelAccessType.ReadWrite,
                       pImageType,
                       pImageChannelOrder,
                       pImageChannelType,
                       pDimensions);
  }

  /**
   * Creates 1D, 2D, or 3D image with a given access policy, channel order,
   * channel data type, and dimensions.
   * 
   * @param pHostAccessType
   *          host access type
   * @param pKernelAccessType
   *          kernel access type
   * @param pImageType
   *          image type (1D, 2D, 3D)
   * @param pImageChannelOrder
   *          channel order
   * @param pImageChannelType
   *          channel data type
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @return 1D,2D, or 3D image
   */
  public ClearCLImage createImage(HostAccessType pHostAccessType,
                                  KernelAccessType pKernelAccessType,
                                  ImageType pImageType,
                                  ImageChannelOrder pImageChannelOrder,
                                  ImageChannelDataType pImageChannelType,
                                  long... pDimensions)
  {
    ClearCLPeerPointer lImage = getBackend().getImagePeerPointer(getPeerPointer(),
                                                                 pHostAccessType,
                                                                 pKernelAccessType,
                                                                 pImageType,
                                                                 pImageChannelOrder,
                                                                 pImageChannelType,
                                                                 pDimensions);

    ClearCLImage lClearCLImage = new ClearCLImage(this,
                                                  lImage,
                                                  pHostAccessType,
                                                  pKernelAccessType,
                                                  pImageType,
                                                  pImageChannelOrder,
                                                  pImageChannelType,
                                                  pDimensions);

    return lClearCLImage;
  }

  /**
   * Creates a blank program, source code must be added to it.
   * 
   * @return program
   */
  public ClearCLProgram createProgram(String... pSourceCode)
  {

    ClearCLProgram lClearCLProgram = new ClearCLProgram(mDevice,
                                                        this,
                                                        null);
    return lClearCLProgram;
  }

  /**
   * Creates a program given a list of resources locate relative to a reference
   * class.
   * 
   * @param pClassForRessource
   *          reference class to locate resources
   * @param pRessourceNames
   *          Resource file names (relative to reference class)
   * @return program
   * @throws IOException
   *           if IO problem while accessing resources
   */
  public ClearCLProgram createProgram(Class<?> pClassForRessource,
                                      String... pRessourceNames) throws IOException
  {
    ClearCLProgram lProgram = createProgram();

    for (String lRessourceName : pRessourceNames)
      lProgram.addSource(pClassForRessource, lRessourceName);

    return lProgram;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return String.format("ClearCLContext [device=%s]",
                         mDevice.toString());
  }

  /* (non-Javadoc)
   * @see clearcl.ClearCLBase#close()
   */
  @Override
  public void close()
  {
    getBackend().releaseContext(getPeerPointer());
  }

}

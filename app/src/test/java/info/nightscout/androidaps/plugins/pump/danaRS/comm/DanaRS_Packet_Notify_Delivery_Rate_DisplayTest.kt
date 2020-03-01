package info.nightscout.androidaps.plugins.pump.danaRS.comm

import android.content.Context
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import info.nightscout.androidaps.interfaces.CommandQueueProvider
import info.nightscout.androidaps.plugins.bus.RxBusWrapper
import info.nightscout.androidaps.plugins.configBuilder.ConfigBuilderPlugin
import info.nightscout.androidaps.plugins.configBuilder.ConstraintChecker
import info.nightscout.androidaps.plugins.configBuilder.ProfileFunction
import info.nightscout.androidaps.plugins.pump.danaRS.DanaRSPlugin
import info.nightscout.androidaps.plugins.treatments.Treatment
import info.nightscout.androidaps.plugins.treatments.TreatmentsPlugin
import info.nightscout.androidaps.utils.DefaultValueHelper
import info.nightscout.androidaps.utils.resources.ResourceHelper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(ConstraintChecker::class, RxBusWrapper::class)
class DanaRS_Packet_Notify_Delivery_Rate_DisplayTest : DanaRSTestBase() {

    @Mock lateinit var defaultValueHelper: DefaultValueHelper
    @Mock lateinit var resourceHelper: ResourceHelper
    @Mock lateinit var profileFunction: ProfileFunction
    @Mock lateinit var configBuilderPlugin: ConfigBuilderPlugin
    @Mock lateinit var treatmentsPlugin: TreatmentsPlugin
    @Mock lateinit var constraintChecker: ConstraintChecker
    @Mock lateinit var commandQueue: CommandQueueProvider
    @Mock lateinit var rxBus: RxBusWrapper
    @Mock lateinit var context: Context
    private lateinit var danaRSPlugin: DanaRSPlugin

    private var treatmentInjector: HasAndroidInjector = HasAndroidInjector {
        AndroidInjector {
            if (it is Treatment) {
                it.defaultValueHelper = defaultValueHelper
                it.resourceHelper = resourceHelper
                it.profileFunction = profileFunction
                it.configBuilderPlugin = configBuilderPlugin
            }
        }
    }

    @Test fun runTest() {
        `when`(resourceHelper.gs(ArgumentMatchers.anyInt(), anyObject())).thenReturn("SomeString")
        // val packet = DanaRS_Packet_Notify_Delivery_Rate_Display(1.0, Treatment(treatmentInjector))
        val packet = DanaRS_Packet_Notify_Delivery_Rate_Display(aapsLogger, rxBus, resourceHelper, danaRSPlugin)
        // test params
        Assert.assertEquals(null, packet.requestParams)
        // test message decoding
// 0% delivered
        packet.handleMessage(createArray(17, 0.toByte()))
        Assert.assertEquals(true, packet.failed)
        // 100 % delivered
        packet.handleMessage(createArray(17, 1.toByte()))
        Assert.assertEquals(false, packet.failed)
        Assert.assertEquals("NOTIFY__DELIVERY_RATE_DISPLAY", packet.friendlyName)
    }

    @Before
    fun mock() {
        danaRSPlugin = DanaRSPlugin(aapsLogger, rxBus, context, resourceHelper, constraintChecker, profileFunction, treatmentsPlugin, sp, commandQueue, danaRPump)
        danaRSPlugin.bolusingTreatment = Treatment(treatmentInjector)
    }
}
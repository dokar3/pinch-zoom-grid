import com.dokar.pinchzoomgrid.PinchItemTransitions
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PinchItemTransitionsTest {
    @Test
    fun testHas() {
        val transitions = PinchItemTransitions.All
        assertTrue(transitions.has(PinchItemTransitions.Scale))
        assertTrue(transitions.has(PinchItemTransitions.Translate))

        assertTrue(PinchItemTransitions.Scale.has(PinchItemTransitions.Scale))
    }

    @Test
    fun testPlus() {
        var next = PinchItemTransitions.None + PinchItemTransitions.Scale
        assertTrue(next.has(PinchItemTransitions.Scale))
        next += PinchItemTransitions.Translate
        assertTrue(next.has(PinchItemTransitions.Scale))
        assertTrue(next.has(PinchItemTransitions.Translate))
    }

    @Test
    fun testMinus() {
        var next = PinchItemTransitions.All - PinchItemTransitions.Scale
        assertFalse(next.has(PinchItemTransitions.Scale))
        next -= PinchItemTransitions.Translate
        assertFalse(next.has(PinchItemTransitions.Scale))
        assertFalse(next.has(PinchItemTransitions.Translate))
    }
}
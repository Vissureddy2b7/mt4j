package pro.laplacelab.bridge.model;

import org.junit.Test;
import pro.laplacelab.bridge.enums.InputType;
import pro.laplacelab.bridge.enums.SignalType;
import pro.laplacelab.bridge.exception.DuplicatePositionException;
import pro.laplacelab.bridge.exception.PositionNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdvisorTest extends PositionTest {

    @Test
    public void whenAdvisorBuildSuccessfullyDataSaved() {
        final Advisor advisor = new Advisor(1L, List.of(
                new Input("key1", "val", InputType.STRING),
                new Input("key2", "1", InputType.NUMBER)));
        assertEquals(Long.valueOf(1), advisor.getMagic());
        assertEquals("val",
                advisor.getInput("key1").orElseThrow().getStringValue());
        assertEquals(new BigDecimal("1"),
                advisor.getInput("key2").orElseThrow().getBigDecimalValue());
    }


    @Test(expected = RuntimeException.class)
    public void whenAdvisorBuildFailThenThrowRuntimeException() {
        new Input("", "val", InputType.STRING);
    }

    @Test
    public void whenAddPositionSuccessThenFindPositionByIdReturnPosition() {
        final Input input = new Input("key1", "val", InputType.STRING);
        final Advisor advisor = new Advisor(1L, List.of(input));
        final Position position = new Position(advisorId, SignalType.BUY, positionId,
                lot, stopLoss, takeProfit, openAt, closeAt, profit);
        advisor.addPosition(position);
        final Position expected = advisor.findPositionById(positionId).orElseThrow();
        assertEquals(position, expected);
    }

    @Test(expected = DuplicatePositionException.class)
    public void whenAddAlreadyExistedPositionTwiceThenThrowDuplicatePositionException() {
        final Input input = new Input("key1", "val", InputType.STRING);
        final Advisor advisor = new Advisor(1L, List.of(input));
        final Position position = new Position(advisorId, SignalType.BUY, positionId,
                lot, stopLoss, takeProfit, openAt, closeAt, profit);
        advisor.addPosition(position);
        advisor.addPosition(position);
    }

    @Test
    public void whenExistedPositionMoveToHistoryThenPositionInHistory() {
        final Input input = new Input("key1", "val", InputType.STRING);
        final Advisor advisor = new Advisor(1L, List.of(input));
        final Position position = new Position(advisorId, SignalType.BUY, positionId,
                lot, stopLoss, takeProfit, openAt, closeAt, profit);
        advisor.addPosition(position);
        advisor.toHistory(position);
        final Optional<Position> origin = advisor.findPositionById(positionId);
        final Position history = advisor.findHistoryById(positionId).orElseThrow();
        assertTrue(origin.isEmpty());
        assertEquals(history, position);
    }

    @Test(expected = PositionNotFoundException.class)
    public void whenMoveToHistoryNotExistedPositionThenThrowPositionNotFoundException() {
        final Input input = new Input("key1", "val", InputType.STRING);
        final Advisor advisor = new Advisor(1L, List.of(input));
        final Position position = new Position(advisorId, SignalType.BUY, positionId,
                lot, stopLoss, takeProfit, openAt, closeAt, profit);
        advisor.toHistory(position);
    }

    @Test
    public void whenUpdateExistedPositionThenPositionSuccessUpdated() {
        final Input input = new Input("key1", "val", InputType.STRING);
        final Advisor advisor = new Advisor(1L, List.of(input));
        final Position origin = new Position(advisorId, SignalType.BUY, positionId,
                lot, stopLoss, takeProfit, openAt, closeAt, profit);
        advisor.addPosition(origin);

        final BigDecimal newLot = new BigDecimal("100");
        final BigDecimal newProfit = new BigDecimal("100");
        final BigDecimal newStopLoss = new BigDecimal("1000");
        final BigDecimal newTakeProfit = new BigDecimal("1000");
        final Long newCloseAt = System.currentTimeMillis();
        final Position forUpdate = new Position(advisorId, SignalType.BUY, positionId,
                newLot, newStopLoss, newTakeProfit, openAt, newCloseAt, newProfit);
        origin.setProfit(new BigDecimal("100"));
        advisor.updatePosition(forUpdate);
        final Position expected = advisor.findPositionById(positionId).orElseThrow();

        assertEquals(newLot, expected.getLot());
        assertEquals(newProfit, expected.getProfit());
        assertEquals(newStopLoss, expected.getStopLoss());
        assertEquals(newTakeProfit, expected.getTakeProfit());
        assertEquals(newCloseAt, expected.getCloseAt());
    }

    @Test(expected = PositionNotFoundException.class)
    public void whenUpdatePositionWhichNotExistedThenThrowPositionNotFoundException() {
        final Input input = new Input("key1", "val", InputType.STRING);
        final Advisor advisor = new Advisor(1L, List.of(input));
        final Position origin = new Position(advisorId, SignalType.BUY, positionId,
                lot, stopLoss, takeProfit, openAt, closeAt, profit);
        advisor.updatePosition(origin);
    }

    @Test
    public void whenCountNotExistDropdownThenReturnZero() {
        final Input input = new Input("key1", "val", InputType.STRING);
        final Advisor advisor = new Advisor(1L, List.of(input));
        final Position dropdown = new Position(advisorId, SignalType.BUY, positionId,
                lot, stopLoss, takeProfit, openAt, closeAt, new BigDecimal("-1"));
        final Position profit = new Position(advisorId, SignalType.BUY, 2L,
                lot, stopLoss, takeProfit, openAt, closeAt, this.profit);
        advisor.addPosition(dropdown);
        advisor.addPosition(profit);
        advisor.toHistory(dropdown);
        advisor.toHistory(profit);
        assertEquals(0, advisor.countDropdown());
    }

    @Test
    public void whenDropdownPositionMoveToHistoryThenDropdownCounterReturnOne() {
        final Input input = new Input("key1", "val", InputType.STRING);
        final Advisor advisor = new Advisor(1L, List.of(input));
        final Position position = new Position(advisorId, SignalType.BUY, positionId,
                lot, stopLoss, takeProfit, openAt, closeAt, new BigDecimal("-1"));
        advisor.addPosition(position);
        advisor.toHistory(position);
        assertEquals(1, advisor.countDropdown());
    }

}
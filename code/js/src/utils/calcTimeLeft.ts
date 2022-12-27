export function calcTimeLeft(timeAvailable: number, startedAt: number): number {
    const now = Date.now() / 1000
    const startedAtSecs = startedAt / 1000
    const timePassed = now - startedAtSecs
    const timeLeft = (timeAvailable - timePassed)
    return Math.min(timeLeft, timeAvailable)
}
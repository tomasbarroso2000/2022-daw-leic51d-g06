export function capitalize(s: string): string {
    const lowercase = s.toLowerCase()
    return lowercase.charAt(0).toUpperCase() + lowercase.slice(1)
}
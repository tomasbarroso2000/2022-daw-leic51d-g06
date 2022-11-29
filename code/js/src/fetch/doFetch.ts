export async function doFetch(url: string): Promise<string | undefined> {
        const resp = await fetch(url)
        const body = await resp.json()
        return JSON.stringify(body)
}
export async function doFetch(
	url: string, 
	options: any | undefined = undefined
) {
	const resp = options ? await fetchWithOptions(url, options) : await fetch(url)
	if (resp.ok) {
		const responseBody = await resp.json()
		return JSON.stringify(responseBody)
	} else {
		if (resp.body) {
			throw await resp.json()
		} else {
			console.log("unauthorized")
			throw "unauthorized"
		}
	}
	
}

async function fetchWithOptions(url: string, options: any) {
	return await fetch(
		url,
		{
			method: options.method,
			body: makeBody(options.body),
			headers: makeHeaders(options.body, options.token)
		}
	)
}

function makeBody(body: any | undefined): string | undefined {
	if (body == undefined)
		return undefined
	return JSON.stringify(body)
}

function makeHeaders(
	body: any | undefined, 
	token: string | undefined
): any {
	const headers = {}
	if (body)
		headers['Content-type'] = 'application/json'
	if (token)
		headers['Authorization'] = `Bearer ${token}`
	return headers
}
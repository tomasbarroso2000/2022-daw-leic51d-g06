import { useState, useEffect } from 'react'

export function useFetch(url: string) : [content: any | undefined, loading: boolean] {
    console.log("url is " + url)
    const [loading, setLoading] = useState(false)
    const [content, setContent] = useState(undefined)
    console.log("content is below:")
    console.log(content)
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            const resp = await fetch(url)
            const body = await resp.json()
            console.log("body below:")
            console.log(body)
            if (!cancelled) {
                setLoading(false)
                setContent(body)
            }
        }
        setLoading(true)
        doFetch()
        return () => {
            cancelled = true
        }
    }, [url, setContent])

    return [JSON.stringify(content), loading]
}
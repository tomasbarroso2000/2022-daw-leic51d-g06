import { useState, useEffect } from 'react'
import { Service } from './Service'

export function askService(service: Service, serviceFunction: () => Promise<any | undefined>): any | undefined {
    const [content, setContent] = useState(undefined)
    useEffect(() => {
        let cancelled = false
        async function doService() {
            const resp = await serviceFunction.call(service)
            if (!cancelled) {
                setContent(resp)
            }
        }
        setContent(undefined)
        doService()
        return () => {
            cancelled = true
        }
    }, [serviceFunction])
    return content
}
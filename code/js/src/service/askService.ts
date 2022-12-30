import { useState, useEffect } from "react";
import { Service } from "./Service";

export type Result<T> =
    | { kind: "success"; result: T }
    | { kind: "error"; error: any };

export function askService(
    service: Service,
    serviceFunction: (...args: any[]) => Promise<any | undefined>,
    ...args: any[]
): Result<any> | undefined {
    const [content, setContent] = useState(undefined);
    useEffect(() => {
        let cancelled = false;
        async function doService() {
            try {
                const resp = await serviceFunction.call(service, ...args);
                if (!cancelled) {
                    setContent(
                        resp != undefined ? { kind: "success", result: resp } : resp
                    );
                }
            } catch (e) {
                setContent({ kind: "error", error: e });
            }
        }
        setContent(undefined);
        doService();
        return () => {
            cancelled = true;
        };
    }, [serviceFunction, ...args]);
    return content;
}
